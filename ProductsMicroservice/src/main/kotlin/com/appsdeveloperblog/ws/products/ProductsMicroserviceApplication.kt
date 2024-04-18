package com.appsdeveloperblog.ws.products

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProductsMicroserviceApplication

fun main(args: Array<String>) {
	runApplication<ProductsMicroserviceApplication>(*args)
}
