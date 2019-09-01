package com.github.seed


class CsvMongoDbImport(folderName: String): CsvImport(folderName) {
    init {
        dbSink = MongoDbSink(configs)
    }
}