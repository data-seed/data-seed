package com.github.seed

import org.bson.Document
import java.io.BufferedReader

class FileResourceReader {

    fun readFile(fileName: String): BufferedReader {
        // TODO change it to use java.nio.* for reading as non-blocking way
        val stream = readFileFromClassPath(fileName) ?: readFileFromJarFileClassPath(fileName)
        return BufferedReader(stream.reader())
    }

    fun asJsonDocument(folderName: String): Document {
        return Document.parse(readFile("/$folderName/config.json").readText())
    }

    private fun readFileFromJarFileClassPath(fileName: String) =
            FileResourceReader::class.java.classLoader.getResourceAsStream(fileName)

    private fun readFileFromClassPath(fileName: String) = javaClass.getResourceAsStream(fileName)

}