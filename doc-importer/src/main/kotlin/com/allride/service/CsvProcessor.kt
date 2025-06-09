package com.allride.service

import com.allride.model.User
import org.springframework.stereotype.Service
import java.io.File

@Service
class CsvProcessor {

    fun parse(filePath: String): List<User> {
        val users = mutableListOf<User>()
        File(filePath).useLines { lines ->
            lines.drop(1).forEachIndexed { idx, line ->
                val rowNumber = idx + 2
                val columns = line.split(",").map { it.trim() }
                if (columns.size == 4) {
                    try {
                        val id = columns[0].toInt()
                        val email = columns[3]
                        if (!email.contains("@"))
                            throw IllegalArgumentException("Invalid email")
                        users.add(User(id, columns[1], columns[2], email))
                    } catch (ex: Exception) {
                        println("Error processing row $rowNumber: $line - ${ex.message}")
                    }
                } else {
                    println("Invalid column count on row $rowNumber: $line")
                }
            }
        }
        return users
    }
}

