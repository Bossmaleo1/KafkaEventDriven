package com.appsdeveloperblog.ws.emailnotification.io

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProcessedEventEntityRepository : JpaRepository<ProcessedEventEntity, Long> {
}