package com.github.seed

import com.opencsv.CSVParser
import reactor.core.publisher.Flux


class CsvReader(private val configs: Configs) {
    private val parser = CSVParser()

    fun parse() : Flux<RecordMap> {
        val fileName = configs.getDataFileName()

        val file = FileResourceReader()
        val header = file.linesAsStream(fileName).findFirst().get()
        val columns = parser.parseLine(header)

        return file.readRecords(fileName)
                .skip(1)
                .map { DataRecord().build(columns,parser.parseLine(it)) }
    }



}
