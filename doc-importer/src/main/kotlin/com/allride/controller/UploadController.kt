package com.allride.controller;

import com.allride.messaging.EventBus
import com.allride.model.FileUploadEvent
import com.allride.model.ProcessingStatus
import com.allride.service.FileStorageService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
class UploadController(
    private val fileStorageService: FileStorageService) {

    @PostMapping("/upload", consumes = ["multipart/form-data"])
    fun uploadCsv(@RequestPart("file") file: MultipartFile): ResponseEntity<String> {
        val isCsv = file.originalFilename?.endsWith(".csv") == true

        if (file.isEmpty || !isCsv) {
            return ResponseEntity.badRequest().body("Invalid file")
        }
        val metadata = fileStorageService.store(file.bytes)
        ProcessingStatus.statusMap[metadata.id] = "PENDING"

        EventBus.fileUploadedChannel.trySend(FileUploadEvent(metadata.id, metadata.path))
        return ResponseEntity.accepted().body(metadata.id)
    }

    @GetMapping("/status")
    fun checkStatus(@RequestParam id: String): ResponseEntity<String> {
        val status = ProcessingStatus.statusMap[id] ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(status)
    }
}