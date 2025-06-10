package com.allride.messaging

import com.allride.model.FileUploadEvent

interface EventPublisher {
    suspend fun publish(event: FileUploadEvent)
}

