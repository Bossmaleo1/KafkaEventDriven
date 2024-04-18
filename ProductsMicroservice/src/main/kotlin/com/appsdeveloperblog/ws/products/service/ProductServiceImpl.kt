package com.appsdeveloperblog.ws.products.service

import com.appsdeveloperblog.ws.core.ProductCreatedEvent
import com.appsdeveloperblog.ws.products.rest.CreateProductRestModel
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.jvm.Throws

@Service
class ProductServiceImpl(val kafkaTemplate: KafkaTemplate<String, ProductCreatedEvent>) : ProductService {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    }


    @Throws(Exception::class)
    override fun createProduct(productRestModel: CreateProductRestModel): String {

        val productId = UUID.randomUUID().toString()

        // TODO: Persist Product Details into database table before publishing an Event

        val productCreatedEvent: ProductCreatedEvent = ProductCreatedEvent(productId,
            productRestModel.title?:"",productRestModel.price?:"", productRestModel.quantity?:0)

        /*val future: CompletableFuture<SendResult<String, ProductCreatedEvent>> =
            kafkaTemplate.send("product-created-events-topic",productId, productCreatedEvent)*/

        LOGGER.info(String.format("Before publishing a ProductCreatedEvent"))

        val record: ProducerRecord<String, ProductCreatedEvent> = ProducerRecord(
            "product-created-events-topic",
            productId,
            productCreatedEvent)
        record.headers().add("messageId", UUID.randomUUID().toString().encodeToByteArray())

        // product-created-events-topic
        val result: SendResult<String, ProductCreatedEvent> =
            kafkaTemplate.send(record).get()

        /*future.whenComplete{ result, exception ->
            if (exception != null){
                LOGGER.error(String.format("Failed to send message : %s", exception.message.toString()))
            } else {
                LOGGER.info(String.format("Successfully sent message : %s", result.recordMetadata.toString()))
            }
        }

        future.join()*/
        LOGGER.info(String.format("Partition: %s", result.recordMetadata.partition()))
        LOGGER.info(String.format("Topic: %s", result.recordMetadata.topic()))
        LOGGER.info(String.format("Offset: %s", result.recordMetadata.offset()))

        LOGGER.info(String.format("****** Returning product id: %s", productId))

        return productId
    }

}