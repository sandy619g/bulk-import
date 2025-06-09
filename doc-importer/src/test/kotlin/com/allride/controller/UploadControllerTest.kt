package com.allride.controller

import com.allride.service.FileStorageService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean

@WebMvcTest(UploadController::class)
class UploadControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var fileStorageService: FileStorageService

    @BeforeEach
    fun setup() {
        com.allride.model.ProcessingStatus.statusMap.clear()
    }

    @Test
    fun `should reject empty file`() {
        val file = MockMultipartFile("file", "empty.csv", "text/csv", ByteArray(0))

        mockMvc.perform(multipart("/api/upload").file(file))
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Invalid file"))
    }

    @Test
    fun `should reject non-CSV`() {
        val file = MockMultipartFile("file", "not.xls", "text/plain", "abc".toByteArray())

        mockMvc.perform(multipart("/api/upload").file(file))
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Invalid file"))
    }

    @Test
    fun `should return status if present`() {
        com.allride.model.ProcessingStatus.statusMap["123"] = "COMPLETED"

        mockMvc.perform(get("/api/status").param("id", "123"))
            .andExpect(status().isOk)
            .andExpect(content().string("COMPLETED"))
    }

    @Test
    fun `should return 404 if status not found`() {
        mockMvc.perform(get("/api/status").param("id", "xyz"))
            .andExpect(status().isNotFound)
    }
}
