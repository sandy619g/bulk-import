package com.allride.service

import com.allride.messaging.EventSubscriber
import com.allride.model.ProcessingStatus
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class FileUploadWorker(
    private val eventSubscriber: EventSubscriber,
    private val processor: CsvProcessor
) {
    @PostConstruct
    fun start() {
        eventSubscriber.subscribe { event ->
            try {
                println("Processing file: $event.filePath")
                val users = processor.parse(event.filePath)
                users.forEach { println("Parsed User: $it") }
                ProcessingStatus.statusMap[event.fileId] = "COMPLETED"
            } catch (e: Exception) {
                println("Error: ${e.message}")
                ProcessingStatus.statusMap[event.fileId] = "FAILED"
            }
        }
    }
}

