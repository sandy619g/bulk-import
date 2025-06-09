package com.allride.service

import com.allride.model.FileMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

@Service
class FileStorageService(
    @Value("\${file.upload-dir:uploads}") private val uploadDir: String
) {
    fun store(fileBytes: ByteArray): FileMetadata {
        val fileId = UUID.randomUUID().toString()
        val uploadsDir = File(uploadDir)
        uploadsDir.mkdirs()

        val storedFile = File(uploadsDir, "$fileId.csv")
        storedFile.writeBytes(fileBytes)

        return FileMetadata(id = fileId, path = storedFile.absolutePath)
    }
}
