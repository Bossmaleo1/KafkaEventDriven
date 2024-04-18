package com.appsdeveloperblog.ws.mockservice

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/response")
class StatusCheckController {

    @GetMapping("/200")
    fun response200String(): ResponseEntity<String> {
        return ResponseEntity.ok().body("200")
    }

    @GetMapping("/500")
    fun response500String(): ResponseEntity<String> {
        return ResponseEntity.internalServerError().build()
    }
}