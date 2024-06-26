package com.appsdeveloperblog.ws.products

import com.appsdeveloperblog.ws.core.ProductCreatedEvent
import com.appsdeveloperblog.ws.products.rest.CreateProductRestModel
import com.appsdeveloperblog.ws.products.service.ProductService
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit


@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test") // application-test.properties
@EmbeddedKafka(partitions = 3, count = 3, controlledShutdown = true)
@SpringBootTest(properties = ["spring.kafka.producer.bootstrap-servers=\${spring.embedded.kafka.brokers}"])
class ProductsServiceIntegrationTest() {
    @Autowired
    private val productService: ProductService? = null

    @Autowired
    private val embeddedKafkaBroker: EmbeddedKafkaBroker? = null

    @Autowired
    var environment: Environment? = null

    private var container: KafkaMessageListenerContainer<String, ProductCreatedEvent>? = null
    private var records: BlockingQueue<ConsumerRecord<String, ProductCreatedEvent>>? = null

    @BeforeAll
    fun setUp() {
        val consumerFactory: DefaultKafkaConsumerFactory<String, Any> = DefaultKafkaConsumerFactory(
            consumerProperties
        )

        val containerProperties: ContainerProperties =
            ContainerProperties(environment!!.getProperty("product-created-events-topic-name"))
        container = KafkaMessageListenerContainer(consumerFactory, containerProperties)
        records = LinkedBlockingQueue()
        container!!.setupMessageListener(MessageListener { e ->
            (records as LinkedBlockingQueue<ConsumerRecord<String, ProductCreatedEvent>>).add(e)
        })
        container!!.start()
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker!!.partitionsPerTopic)
    }

    @Test
    @Throws(Exception::class)
    fun testCreateProduct_whenGivenValidProductDetails_successfullySendsKafkaMessage() {
        // Arrange

        val title = "iPhone 11"
        val price = BigDecimal(600)
        val quantity = 1

        val createProductRestModel: CreateProductRestModel = CreateProductRestModel()
        createProductRestModel.price = price.toString()
        createProductRestModel.quantity = quantity
        createProductRestModel.title = title


        // Act
        productService!!.createProduct(createProductRestModel)


        // Assert
        val message: ConsumerRecord<String, ProductCreatedEvent> = records!!.poll(3000, TimeUnit.MILLISECONDS)
        Assertions.assertNotNull(message)
        Assertions.assertNotNull(message.key())
        val productCreatedEvent: ProductCreatedEvent = message.value()
        Assertions.assertEquals(createProductRestModel.quantity, productCreatedEvent.quantity)
        Assertions.assertEquals(createProductRestModel.title, productCreatedEvent.title)
        Assertions.assertEquals(createProductRestModel.price, productCreatedEvent.price)
    }


    private val consumerProperties: Map<String, Any?>
        get() = java.util.Map.of<String, Any?>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            embeddedKafkaBroker!!.brokersAsString,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            ErrorHandlingDeserializer::class.java,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
            JsonDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG,
            environment!!.getProperty("spring.kafka.consumer.group-id"),
            JsonDeserializer.TRUSTED_PACKAGES,
            environment!!.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"),
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            environment!!.getProperty("spring.kafka.consumer.auto-offset-reset")
        )

    @AfterAll
    fun tearDown() {
        container!!.stop()
    }
}
