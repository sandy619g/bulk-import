package com.allride.messaging

import com.allride.model.FileUploadEvent

interface EventSubscriber {
    fun subscribe(handler: (FileUploadEvent) -> Unit)
}