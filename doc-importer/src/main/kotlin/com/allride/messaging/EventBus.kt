package com.allride.messaging

import com.allride.model.FileUploadEvent
import kotlinx.coroutines.channels.Channel

object EventBus {
    var fileUploadedChannel: Channel<FileUploadEvent> = Channel(Channel.UNLIMITED)

    fun reset() {
        fileUploadedChannel = Channel(Channel.UNLIMITED)
    }
}


