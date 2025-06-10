package com.allride.messaging

import com.allride.model.FileUploadEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class EventBus : EventPublisher, EventSubscriber {
    private var fileUploadedChannel = Channel<FileUploadEvent>(Channel.UNLIMITED)

    override suspend fun publish(event: FileUploadEvent) {
        fileUploadedChannel.send(event)
    }

    override fun subscribe(handler: (FileUploadEvent) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            for (event in fileUploadedChannel) {
                handler(event)
            }
        }
    }

}


