package com.allride.service

import com.allride.messaging.EventSubscriber
import com.allride.model.FileUploadEvent
import com.allride.model.ProcessingStatus
import com.allride.model.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import kotlin.test.assertEquals

class FileUploadWorkerTest {

    private lateinit var eventSubscriber: EventSubscriber
    private lateinit var processor: CsvProcessor
    private lateinit var fileUploadWorker: FileUploadWorker

    @BeforeEach
    fun setUp() {
        eventSubscriber = mock()
        processor = mock()
        fileUploadWorker = FileUploadWorker(eventSubscriber, processor)
    }

    @Test
    fun `should mark status COMPLETED on successful processing`() {
        val event = FileUploadEvent("file123", "/path/to/file.csv")
        val users = listOf(
            User(1, "John", "Doe", "john@test.com"),
            User(2, "Jane", "Doe", "jane@test.com")
        )

        whenever(processor.parse(event.filePath)).thenReturn(users)

        val captor = argumentCaptor<(FileUploadEvent) -> Unit>()
        fileUploadWorker.start()
        verify(eventSubscriber).subscribe(captor.capture())

        // Trigger the handler manually
        captor.firstValue.invoke(event)

        assertEquals("COMPLETED", ProcessingStatus.statusMap[event.fileId])
        verify(processor).parse(event.filePath)
    }

    @Test
    fun `should mark status FAILED if exception occurs`() {
        val event = FileUploadEvent("file456", "/bad/path.csv")

        whenever(processor.parse(event.filePath)).thenThrow(RuntimeException("Parsing error"))

        val captor = argumentCaptor<(FileUploadEvent) -> Unit>()
        fileUploadWorker.start()
        verify(eventSubscriber).subscribe(captor.capture())

        // Trigger the handler manually
        captor.firstValue.invoke(event)

        assertEquals("FAILED", ProcessingStatus.statusMap[event.fileId])
        verify(processor).parse(event.filePath)
    }
}
