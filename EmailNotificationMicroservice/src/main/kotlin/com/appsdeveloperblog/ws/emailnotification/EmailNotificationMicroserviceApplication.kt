package com.appsdeveloperblog.ws.emailnotification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class EmailNotificationMicroserviceApplication {
	@Bean
	fun restTemplate(): RestTemplate {
		return RestTemplate()
	}
}

fun main(args: Array<String>) {
	runApplication<EmailNotificationMicroserviceApplication>(*args)
}


