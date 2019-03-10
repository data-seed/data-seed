package com.github.seed

import com.opencsv.CSVParser
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import java.util.stream.Stream


class CsvReader(private val configs: Configs) {
    private val parser = CSVParser()

    fun parse() : Flux<RecordMap> {
        val fileName = configs.getDataFileName()

        val header = linesAsStream(fileName).findFirst().get()
        val columns = parser.parseLine(header)

        return readRecords(fileName)
                .skip(1)
                .map { DataRecord().build(columns,parser.parseLine(it)) }
    }

    private fun readRecords(fileName: String): Flux<String> {
        return linesAsStream(fileName).toFlux()
    }

    private fun linesAsStream(fileName: String): Stream<String> {
        val bufferedReader = FileResourceReader().readFile(fileName)
        return bufferedReader.lines()
    }


}
