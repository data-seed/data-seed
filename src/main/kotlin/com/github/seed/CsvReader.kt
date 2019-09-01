package com.github.seed

import com.opencsv.CSVParser
import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicInteger


class CsvReader(private val configs: Configs) {
    private val dataFile = FileResourceReader(configs.getDataFileName())
    private val parser = CSVParser()

    fun parse() : Flux<RecordMap> {
        val header = dataFile.linesAsStream().findFirst().get()
        val columns = parser.parseLine(header)
        var index = AtomicInteger(0)
        return dataFile.readRecords()
                .skip(1)
                .map {
                    DataRecord(index.incrementAndGet()).build(columns,parser.parseLine(it))
                }
    }

    fun checksum(): String {
        return dataFile.checksum()
    }


}
