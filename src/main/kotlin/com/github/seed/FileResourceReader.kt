package com.github.seed

import org.bson.Document
import reactor.core.publisher.Flux
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.stream.Stream

class FileResourceReader(private val fileName: String) {

    fun readFileAsText() = String(Files.readAllBytes(getFilePath()))

    fun readRecords(): Flux<String> {
        return Flux.using( { Files.lines(getFilePath()) }, { Flux.fromStream(it) }, { it.close() })
    }

    fun linesAsStream(): Stream<String> {
        return Files.lines(getFilePath())
    }

    fun asDocument(): Document {
        return Document.parse(readFileAsText())
    }

    private fun getFilePath(): Path {
        return Paths.get(javaClass.getResource(fileName).toURI())
    }

    fun checksum(): String {
        // TODO: Optimize calculation of MD5 to read file only once
        val md5 = MessageDigest.getInstance("MD5")!!
        md5.update(Files.readAllBytes(getFilePath()))
        val sb = StringBuilder()
        for (b in md5.digest()) sb.append(String.format("%02x", b))
        return sb.toString().toUpperCase()
    }

}