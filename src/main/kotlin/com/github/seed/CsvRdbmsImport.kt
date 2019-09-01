package com.github.seed


class CsvRdbmsImport(folderName: String): CsvImport(folderName) {
    init {
        dbSink = RdbmsDbSink(configs)
    }
}