package com.appsdeveloperblog.ws.products

import com.appsdeveloperblog.ws.core.ProductCreatedEvent
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.Map
import java.util.HashMap

@Configuration
class KafkaConfig {

    @Value("\${spring.kafka.producer.bootstrap-servers}")
    val bootstrapServers : String? = null

    @Value("\${spring.kafka.producer.key-serializer}")
    val keySerializer : String? = null

    @Value("\${spring.kafka.producer.value-serializer}")
    val valueSerializer : String? = null

    @Value("\${spring.kafka.producer.acks}")
    val acks: String? = null

    @Value("\${spring.kafka.producer.properties.delivery.timeout.ms}")
    val deliveryTimeout : String? = null

    @Value("\${spring.kafka.producer.properties.linger.ms}")
    val linger: String? = null

    @Value("\${spring.kafka.producer.properties.request.timeout.ms}")
    val requestTimeout: String ? = null

    @Value("\${spring.kafka.producer.properties.enable.idempotence}")
    val idempotence: String? = null

    @Value("\${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    val inflightRequests: Int? = null

    fun producerConfigs(): HashMap<String, Any> {
        val config = HashMap<String, Any>()

        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers!!
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = keySerializer!!
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = valueSerializer!!
        config[ProducerConfig.ACKS_CONFIG] = acks!!
        config[ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG] = deliveryTimeout!!
        config[ProducerConfig.LINGER_MS_CONFIG] = linger!!
        config[ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG] = requestTimeout!!
        config[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = idempotence!!
        config[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = inflightRequests!!
        //config[CommonClientConfigs.RETRIES_CONFIG] = Int.MAX_VALUE

        return config
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, ProductCreatedEvent> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, ProductCreatedEvent> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun createTopic(): NewTopic {
        return TopicBuilder.name("product-created-events-topic")
            .partitions(3)
            .replicas(3)
            .configs(Map.of("min.insync.replicas","2"))
            .build()
    }
}