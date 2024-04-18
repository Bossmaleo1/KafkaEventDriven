package com.appsdeveloperblog.ws.emailnotification.io

import jakarta.persistence.*
import lombok.*
import java.io.Serializable
import java.math.BigDecimal

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "processed_events")
data class ProcessedEventEntity(
    @Id
    @GeneratedValue
    @Column(unique = true)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    val messageId: String? = null,
    @Column(nullable = false)
    val productId: String? = null,
): Serializable








/*@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
data class Employee (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val firstName: String,
    val lastName: String,
    @Column(nullable = false, unique = true)
    val email: String,
    val departmentCode: String,
    val organizationCode: String
)*/
