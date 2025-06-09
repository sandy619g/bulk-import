package com.allride.service

import org.junit.jupiter.api.*
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileStorageServiceTest {

    @TempDir
    lateinit var tempDir: Path

    private lateinit var service: FileStorageService

    @BeforeEach
    fun setUp() {
        service = FileStorageService(tempDir.toString())
    }

    @Test
    fun `store should save non-empty file and return correct metadata`() {
        val content = "sample content".toByteArray()

        val metadata = service.store(content)
        val file = File(metadata.path)

        assertTrue(file.exists())
        assertEquals(content.toList(), file.readBytes().toList())
        assertTrue(metadata.id.isNotBlank())
    }

    @Test
    fun `store should handle empty file content`() {
        val emptyContent = ByteArray(0)

        val metadata = service.store(emptyContent)
        val file = File(metadata.path)

        assertTrue(file.exists())
        assertEquals(0, file.length())
    }

    @AfterEach
    fun cleanup() {
        tempDir.toFile().deleteRecursively()
    }
}

