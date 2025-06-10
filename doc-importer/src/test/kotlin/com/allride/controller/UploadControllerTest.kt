package com.allride.controller

import com.allride.messaging.EventPublisher
import com.allride.model.FileMetadata
import com.allride.model.FileUploadEvent
import com.allride.model.ProcessingStatus
import com.allride.service.FileStorageService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile

class UploadControllerTest {

    private lateinit var fileStorageService: FileStorageService
    private lateinit var eventPublisher: EventPublisher
    private lateinit var uploadController: UploadController

    @BeforeEach
    fun setUp() {
        fileStorageService = mock()
        eventPublisher = mock()
        uploadController = UploadController(fileStorageService, eventPublisher)
    }

    @Test
    fun `uploadCsv should return bad request for invalid file`() = runTest {
        val file: MultipartFile = MockMultipartFile("file", "", "text/csv", ByteArray(0))

        val response = uploadController.uploadCsv(file)

        assertEquals(400, response.statusCodeValue)
        assertEquals("Invalid file", response.body)
    }

    @Test
    fun `uploadCsv should accept valid csv and publish event`() = runTest {
        val fileContent = "id,name\n1,Test".toByteArray()
        val file: MultipartFile = MockMultipartFile("file", "data.csv", "text/csv", fileContent)

        val fileId = "123"
        val filePath = "/uploads/data.csv"

        whenever(fileStorageService.store(any())).thenReturn(FileMetadata(fileId, filePath))

        val response = uploadController.uploadCsv(file)

        assertEquals(202, response.statusCodeValue)
        assertEquals(fileId, response.body)

        assertEquals("PENDING", ProcessingStatus.statusMap[fileId])
        verify(eventPublisher).publish(FileUploadEvent(fileId, filePath))
    }

    @Test
    fun `checkStatus should return status if present`() {
        val id = "123"
        ProcessingStatus.statusMap[id] = "COMPLETED"

        val response: ResponseEntity<String> = uploadController.checkStatus(id)

        assertEquals(200, response.statusCodeValue)
        assertEquals("COMPLETED", response.body)
    }

    @Test
    fun `checkStatus should return 404 if id not found`() {
        val response: ResponseEntity<String> = uploadController.checkStatus("not-found-id")

        assertEquals(404, response.statusCodeValue)
        assertNull(response.body)
    }
}

