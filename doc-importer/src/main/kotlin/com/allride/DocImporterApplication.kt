package com.allride

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DocImporterApplication

fun main(args: Array<String>) {
	runApplication<DocImporterApplication>(*args)
}
