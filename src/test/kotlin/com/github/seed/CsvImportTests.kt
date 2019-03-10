package com.github.seed

import com.mongodb.reactivestreams.client.MongoClients
import io.kotlintest.assertSoftly
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
import reactor.test.StepVerifier

class CsvImportTests : StringSpec() {

    override fun listeners(): List<TestListener> = listOf(EmbedMongoDbListener)

    init {
        "read and import csv file with multiple records" {

            val client = MongoClients.create("mongodb://localhost:27017")
            val database = client.getDatabase("masters")
            val collection = database.getCollection("masters")
            collection.drop().toMono().block()

            CsvImport("cities").import().blockLast()

            val documents = collection.find().toFlux()
            StepVerifier.create(documents)
                    .assertNext {
                        assertSoftly {
                            it.getString("type") shouldBe "city"
                            it.getString("uniqueId") shouldBe "022"
                            it.getString("name") shouldBe "Mumbai"
                            it.getString("state") shouldBe "Maharashtra"
                            it.getInteger("rank") shouldBe 100
                            it.getBoolean("active") shouldBe true
                        }
                    }
                    .assertNext {
                        assertSoftly {
                            it.getString("type") shouldBe "city"
                            it.getString("uniqueId") shouldBe "020"
                            it.getString("name") shouldBe "Pune"
                            it.getString("state") shouldBe "Maharashtra"
                            it.getInteger("rank") shouldBe 0
                            it.getBoolean("active") shouldBe true
                        }
                    }
                    .expectComplete()
                    .verify()

        }
    }
}