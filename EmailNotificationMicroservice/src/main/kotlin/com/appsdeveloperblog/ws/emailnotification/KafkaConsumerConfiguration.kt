package com.appsdeveloperblog.ws.emailnotification

import com.appsdeveloperblog.ws.emailnotification.error.NotRetryableException
import com.appsdeveloperblog.ws.emailnotification.error.RetryableException
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import java.util.HashMap
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.util.backoff.FixedBackOff
import org.springframework.web.client.HttpServerErrorException

@Configuration
class KafkaConsumerConfiguration(
    @Autowired
    val environment: Environment
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val config: HashMap<String, Any> = HashMap<String, Any>()
        config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = environment.getProperty("spring.kafka.consumer.bootstrap-servers")!!
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
        config[ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS] = JsonDeserializer::class.java
        config[JsonDeserializer.TRUSTED_PACKAGES] = environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages")!!
        config[ConsumerConfig.GROUP_ID_CONFIG] = environment.getProperty("consumer.group-id")!!

        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Any>,
        kafkaTemplate: KafkaTemplate<String, Any>
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val errorHandler = DefaultErrorHandler(DeadLetterPublishingRecoverer(kafkaTemplate),
            FixedBackOff(5000,3))
        errorHandler.addNotRetryableExceptions(NotRetryableException::class.java)
        errorHandler.addRetryableExceptions(RetryableException::class.java)


        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory
        factory.setCommonErrorHandler(errorHandler)

        return factory
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, Any>): KafkaTemplate<String, Any> {
        val config: HashMap<String, Any> = HashMap<String, Any>()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = environment.getProperty("spring.kafka.consumer.bootstrap-servers")!!
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return KafkaTemplate(producerFactory)
    }
}