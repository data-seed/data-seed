package com.github.seed

import org.bson.Document
import reactor.core.publisher.Flux
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream


class FileResourceReader {

    fun readFileAsText(fileName: String) = String(Files.readAllBytes(getFilePath(fileName)))

    fun readRecords(fileName: String): Flux<String> {
        return Flux.using( { Files.lines(getFilePath(fileName)) }, { Flux.fromStream(it) }, { it.close() })
    }

    fun linesAsStream(fileName: String): Stream<String> {
        return Files.lines(getFilePath(fileName))
    }

    fun asDocument(fileName: String): Document {
        return Document.parse(readFileAsText(fileName))
    }

    private fun getFilePath(fileName: String): Path {
        return Paths.get(javaClass.getResource(fileName).toURI())
    }

}