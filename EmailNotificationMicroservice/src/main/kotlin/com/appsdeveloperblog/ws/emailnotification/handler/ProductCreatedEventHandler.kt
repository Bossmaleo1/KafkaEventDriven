package com.appsdeveloperblog.ws.emailnotification.handler

import com.appsdeveloperblog.ws.core.ProductCreatedEvent
import com.appsdeveloperblog.ws.emailnotification.error.NotRetryableException
import com.appsdeveloperblog.ws.emailnotification.error.RetryableException
import com.appsdeveloperblog.ws.emailnotification.io.ProcessedEventEntity
import com.appsdeveloperblog.ws.emailnotification.io.ProcessedEventEntityRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate

@Component
@KafkaListener(topics = ["product-created-events-topic"])
class ProductCreatedEventHandler(
    val restTemplate: RestTemplate,
    val processedEventEntityRepository: ProcessedEventEntityRepository
) {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Transactional
    @KafkaHandler
    fun handle(@Payload productCreatedEvent: ProductCreatedEvent,
               @Header(value = "messageId") messageId: String,
               @Header(KafkaHeaders.RECEIVED_KEY) messageKey: String) {
        LOGGER.info(String.format("Received a new event: %s with productId: %s", productCreatedEvent.title, productCreatedEvent.productId))

        // Check if this message was already processed before
        val existingRecord: ProcessedEventEntity? = processedEventEntityRepository.findByMessageId(messageId = messageId)
        if (existingRecord != null) {
            LOGGER.info(String.format("Found a duplicate message id: %s", existingRecord.messageId))
            return
        }
        val requestUrl = "http://localhost:8082/response/200"

        try {
            val response: ResponseEntity<String> =
                restTemplate.exchange(requestUrl, HttpMethod.GET, null, String::class.java)

            if (response.statusCode.value() == HttpStatus.OK.value()) {
                LOGGER.info(String.format("Received response from a remote service: %s", response.body))
            }
        } catch (ex: ResourceAccessException) {
            LOGGER.error(ex.message)
            throw RetryableException(ex)
        } catch (ex: HttpServerErrorException) {
            LOGGER.error(ex.message)
            throw NotRetryableException(ex)
        }catch (ex: Exception) {
            LOGGER.error(ex.message)
            throw NotRetryableException(ex)
        }

        // Save a unique message id in a database table
        try {
            val processedEventEntity =  ProcessedEventEntity(
            messageId = messageId,
            productId = productCreatedEvent.productId)
            LOGGER.info(String.format("BOSSMALEO Testing Testing : %s with %s", processedEventEntity.messageId, processedEventEntity.productId))
            processedEventEntityRepository
                .save(processedEventEntity)
        } catch (ex: DataIntegrityViolationException) {
            throw NotRetryableException(ex)
        }


    }
}