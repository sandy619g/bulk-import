package com.allride.service

import com.allride.messaging.EventBus
import com.allride.model.FileUploadEvent
import com.allride.model.ProcessingStatus
import com.allride.model.User
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import kotlin.test.assertEquals

class FileUploadWorkerTest {

    private lateinit var worker: FileUploadWorker
    private lateinit var processor: CsvProcessor

    @BeforeEach
    fun setUp() {
        processor = mock(CsvProcessor::class.java)
        worker = FileUploadWorker(processor)
        ProcessingStatus.statusMap.clear()

        EventBus.fileUploadedChannel = Channel(Channel.UNLIMITED)
    }


    @Test
    fun `processes event and updates status to COMPLETED`() = runBlocking {
        val fileId = "file123"
        val path = "/fake/file.csv"
        val event = FileUploadEvent(fileId, path)

        `when`(processor.parse(path)).thenReturn(listOf(User(1, "A", "B", "a@b.com")))

        val job = CoroutineScope(Dispatchers.IO).launch {
            worker.start()
        }

        EventBus.fileUploadedChannel.send(event)

        delay(300) // Wait for processing
        assertEquals("COMPLETED", ProcessingStatus.statusMap[fileId])

        job.cancel()
    }

    @Test
    fun `sets status to FAILED on exception`() = runBlocking {
        val fileId = "file456"
        val path = "/bad/file.csv"
        val event = FileUploadEvent(fileId, path)

        `when`(processor.parse(path)).thenThrow(RuntimeException("Boom"))

        val job = CoroutineScope(Dispatchers.IO).launch {
            worker.start()
        }

        EventBus.fileUploadedChannel.send(event)

        delay(300)
        assertEquals("FAILED", ProcessingStatus.statusMap[fileId])

        job.cancel()
    }
}
