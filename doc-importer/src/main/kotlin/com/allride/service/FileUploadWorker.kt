package com.allride.service

import com.allride.messaging.EventBus
import com.allride.model.ProcessingStatus
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class FileUploadWorker(
    private val processor: CsvProcessor,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO))
{
    @PostConstruct
    fun start() {
        scope.launch {
            for (event in EventBus.fileUploadedChannel) {
                val fileId = event.fileId
                val filePath = event.filePath

                try {
                    println("Processing file: $filePath")
                    val users = processor.parse(filePath)
                    users.forEach { println("Parsed user: $it") }
                    ProcessingStatus.statusMap[fileId] = "COMPLETED"
                } catch (e: Exception) {
                    println("Failed to process file $fileId: ${e.message}")
                    ProcessingStatus.statusMap[fileId] = "FAILED"
                }
            }
        }
    }
}
