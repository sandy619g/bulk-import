package com.allride.service

import com.allride.model.User
import org.junit.jupiter.api.*
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CsvProcessorTest {

    private val processor = CsvProcessor()
    private lateinit var tempFile: File

    @AfterEach
    fun cleanup() {
        tempFile.delete()
    }

    private fun createTempCsv(content: String): File {
        return File.createTempFile("test-", ".csv").apply {
            writeText(content.trimIndent())
            deleteOnExit()
            tempFile = this
        }
    }

    @Test
    fun `parse valid CSV returns correct users`() {
        val csv = """
            id,firstName,lastName,email
            1,John,Doe,john@example.com
            2,Jane,Doe,jane@example.com
        """
        val file = createTempCsv(csv)

        val users = processor.parse(file.absolutePath)

        assertEquals(2, users.size)
        assertEquals(User(1, "John", "Doe", "john@example.com"), users[0])
        assertEquals(User(2, "Jane", "Doe", "jane@example.com"), users[1])
    }

    @Test
    fun `parse skips row with invalid column count`() {
        val csv = """
            id,firstName,lastName,email
            1,John,Doe,john@example.com
            2,InvalidRowOnly3Fields
            3,Jane,Doe,jane@example.com
        """
        val file = createTempCsv(csv)

        val users = processor.parse(file.absolutePath)

        assertEquals(2, users.size)
        assertTrue(users.any { it.id == 1 })
        assertTrue(users.any { it.id == 3 })
    }

    @Test
    fun `parse skips row with non-numeric ID`() {
        val csv = """
            id,firstName,lastName,email
            abc,John,Doe,john@example.com
            2,Jane,Doe,jane@example.com
        """
        val file = createTempCsv(csv)

        val users = processor.parse(file.absolutePath)

        assertEquals(1, users.size)
        assertEquals(2, users.first().id)
    }

    @Test
    fun `parse skips row with invalid email`() {
        val csv = """
            id,firstName,lastName,email
            1,John,Doe,john[at]example.com
            2,Jane,Doe,jane@example.com
        """
        val file = createTempCsv(csv)

        val users = processor.parse(file.absolutePath)

        assertEquals(1, users.size)
        assertEquals(2, users.first().id)
    }

    @Test
    fun `parse throws exception on empty file`() {
        val file = createTempCsv("")

        val exception = assertThrows<Exception> {
            processor.parse(file.absolutePath)
        }

        assertEquals("No valid rows found all rows are corrupted.", exception.message)
    }


    @Test
    fun `parse skips empty and whitespace lines`() {
        val csv = """
            id,firstName,lastName,email
            
            1,John,Doe,john@example.com
                 
            2,Jane,Doe,jane@example.com
        """
        val file = createTempCsv(csv)

        val users = processor.parse(file.absolutePath)

        assertEquals(2, users.size)
        assertTrue(users.any { it.id == 1 })
        assertTrue(users.any { it.id == 2 })
    }
}
