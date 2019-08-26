package com.github.seed

import com.mongodb.client.model.Sorts.descending
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.Success
import org.bson.Document
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.time.LocalDateTime

class MongoDbSink(private val configs: Configs) {
    private val client: MongoClient = MongoClients.create(System.getenv("DB_URL") ?: "mongodb://localhost:27017")
    private val database = client.getDatabase(configs.getDatabaseName())
    private val collection = database.getCollection(configs.getCollectionName())
    private val schema = database.getCollection("dataSeedSchemaHistory")

    fun dropData() {
        collection.deleteMany(configs.getDropQuery()).toMono()
                .subscribe { println("${it.deletedCount} records deleted for ${configs.seedName()}.") }
    }

    fun save(record: RecordJson): Mono<Success> {
        return collection.insertOne(Document(record)).toMono()
    }

    fun updateSeedHistory(checksum: String) {
        println ("Inserting dataSeedSchemaHistory for executing seed ${configs.seedName()}.")
        val execution = Document( mapOf("seedName" to configs.seedName(),
                "checksum" to checksum,
                "timestamp" to LocalDateTime.now()
        ))
        schema.insertOne(execution).toMono().block()
    }

    fun close() {
        println ("Closing database connection for ${configs.seedName()}.")
        client.close()
    }

    fun isSeedChanged(checksum: String): Boolean {
        val document = schema.find(Document("seedName", configs.seedName()))
                .sort(descending("timestamp"))
                .first().toMono().block()
        return document?.get("checksum") != checksum
    }

}
