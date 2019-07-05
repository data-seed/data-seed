package com.github.seed

import com.mongodb.reactivestreams.client.Success
import reactor.core.publisher.Flux


class CsvImport(folderName: String) {
    private val configs = Configs(folderName)
    private val reader = CsvReader(configs)
    private val validator = RecordValidator(configs)
    private val generator = RecordGenerator(configs.getTemplate())
    private val database = MongoDbSink(configs)

    fun import(): Flux<Success> {
        return reader.parse()
                .map { validator.validate(it) }
                .map { generator.generateAsBson(it) }
                .concatMap { database.save(it) }
                .doOnComplete { database.close() }
                .doOnError { println(it.message) }
    }
}