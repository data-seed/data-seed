package com.github.seed

import com.mongodb.reactivestreams.client.MongoClients
import io.kotlintest.assertSoftly
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import reactor.core.publisher.toFlux
import reactor.test.StepVerifier

class CsvMongoDbImportTests : StringSpec() {

    override fun listeners(): List<TestListener> = listOf(EmbedMongoDbListener)

    init {
        "read and import csv file with multiple records" {
            val result = CsvMongoDbImport("cities").import().block()!!
            result shouldBe ImportResult.Success

            val client = MongoClients.create("mongodb://localhost:27017")
            val database = client.getDatabase("masters")
            val documents = database.getCollection("masters").find().toFlux()
            StepVerifier.create(documents)
                    .assertNext {
                        assertSoftly {
                            it["type"] shouldBe "city"
                            it["uniqueId"] shouldBe "022"
                            it["name"] shouldBe "Mumbai"
                            it["state"] shouldBe "Maharashtra"
                            it["rank"] shouldBe 100
                            it["active"] shouldBe true
                        }
                    }
                    .assertNext {
                        assertSoftly {
                            it["type"] shouldBe "city"
                            it["uniqueId"] shouldBe "020"
                            it["name"] shouldBe "Pune"
                            it["state"] shouldBe "Maharashtra"
                            it["rank"] shouldBe 0
                            it["active"] shouldBe true
                        }
                    }
                    .expectComplete()
                    .verify()
        }

        "import should return skipped on second run" {
            var result = CsvMongoDbImport("cities").import().block()!!
            result shouldBe ImportResult.Success
            result = CsvMongoDbImport("cities").import().block()!!
            result shouldBe ImportResult.Skipped
        }

        "import should execute seed when data is changed" {
            var result = CsvMongoDbImport("cities").import().block()!!
            result shouldBe ImportResult.Success
            result = CsvMongoDbImport("cities-more").import().block()!!
            result shouldBe ImportResult.Success
        }
    }
}