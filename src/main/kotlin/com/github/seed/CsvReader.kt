package com.github.seed

import com.opencsv.CSVParser
import reactor.core.publisher.Flux


class CsvReader(private val configs: Configs) {
    private val dataFile = FileResourceReader(configs.getDataFileName())
    private val parser = CSVParser()

    fun parse() : Flux<RecordMap> {
        val header = dataFile.linesAsStream().findFirst().get()
        val columns = parser.parseLine(header)

        return dataFile.readRecords()
                .skip(1)
                .map { DataRecord().build(columns,parser.parseLine(it)) }
    }

    fun checksum(): String {
        return dataFile.checksum()
    }


}
