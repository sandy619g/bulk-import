package com.allride.service

import com.allride.model.User
import org.springframework.stereotype.Service
import java.io.File

@Service
class CsvProcessor {

    fun parse(filePath: String): List<User> {
        val validUsers = mutableListOf<User>()
        val errorRows = mutableListOf<String>()

        File(filePath).useLines { lines ->
            val iterator = lines.iterator()

            if (iterator.hasNext()) iterator.next()

            var rowNum = 1
            while (iterator.hasNext()) {
                val line = iterator.next()
                rowNum++

                try {
                    val user = parseRow(line)
                    validUsers.add(user)
                } catch (e: Exception) {
                    println("Error parsing row $rowNum: ${e.message} — data: $line")
                    errorRows.add("Row $rowNum: ${e.message}")
                }
            }
        }

        if (validUsers.isEmpty()) {
            throw Exception("No valid rows found all rows are corrupted.")
        }

        if (errorRows.isNotEmpty()) {
            println("Parsing completed with errors in ${errorRows.size} rows.")
        }

        return validUsers
    }

    private fun parseRow(row: String): User {
        val parts = row.split(",")
        if (parts.size != 4) throw IllegalArgumentException("Incorrect number of columns")

        val id = parts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid id")
        val firstName = parts[1].trim()
        val lastName = parts[2].trim()
        val email = parts[3].trim()
        //TODO add email validator
        if (!email.contains("@")) throw IllegalArgumentException("Invalid email format")

        return User(id, firstName, lastName, email)
    }
}


