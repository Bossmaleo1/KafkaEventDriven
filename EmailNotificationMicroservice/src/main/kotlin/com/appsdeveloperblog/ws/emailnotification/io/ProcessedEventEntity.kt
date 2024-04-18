package com.appsdeveloperblog.ws.emailnotification.io

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.io.Serializable
import java.math.BigDecimal

@Entity
@Table(name = "processed_events")
class ProcessedEventEntity: Serializable {

    @Id
    @GeneratedValue
    var id: Long? = null

    @Column(nullable = false, unique = true)
    var messageId: String? = null
    @Column(nullable = false)
    var productId: String? = null

    constructor() {}

    constructor(id: Long?, messageId: String?, productId: String?) {
        this.id = id
        this.messageId = messageId
        this.productId = productId
    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -227264951080660124L
    }

}