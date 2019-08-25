package com.github.seed

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.Success
import org.bson.Document
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

class MongoDbSink(private val configs: Configs) {
    private val client: MongoClient = MongoClients.create(System.getenv("DB_URL") ?: "mongodb://localhost:27017")
    private val database = client.getDatabase(configs.getDatabaseName())
    private val collection = database.getCollection(configs.getCollectionName())

    init {
        if (configs.isCleanupRequired()) {
            collection.deleteMany(configs.getDropQuery()).toMono()
                    .subscribe { println("${it.deletedCount} records deleted for ${configs.seedName}.") }
        }
    }

    fun save(record: RecordJson): Mono<Success> {
        return collection.insertOne(Document(record)).toMono()
    }

    fun close() {
        println ("Closing database connection for ${configs.seedName}.")
        client.close()
    }

}
