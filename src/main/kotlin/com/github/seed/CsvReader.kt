package com.github.seed

import com.opencsv.CSVParser
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import java.io.BufferedReader
import java.io.InputStream
import java.util.stream.Stream


class CsvReader(private val folderName: String) {
    private val parser = CSVParser()

    fun parse() : Flux<Map<String,Any>> {
        val header = linesAsStream("/$folderName/data.csv").findFirst().get()
        val columns = parser.parseLine(header)

        return readRecords("/$folderName/data.csv")
                .skip(1)
                .map { DataRecord().build(columns,parser.parseLine(it)) }
    }

    private fun readRecords(fileName: String): Flux<String> {
        return linesAsStream(fileName).toFlux()
    }

    private fun linesAsStream(fileName: String): Stream<String> {
        val stream: InputStream =
                javaClass.getResourceAsStream(fileName)
                        ?: CsvReader::class.java.classLoader.getResourceAsStream(fileName)
        return BufferedReader(stream.reader()).lines()
    }

}
