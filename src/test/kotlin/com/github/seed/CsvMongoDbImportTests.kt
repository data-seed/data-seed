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
        "should validate successfully for correct data file" {
            val result = CsvMongoDbImport("cities").import(true)
            StepVerifier.create(result).assertNext { it shouldBe ImportResult.Validated }.expectComplete().verify()
        }

        "should show validation error for incorrect data file" {
            val csvImport = CsvMongoDbImport("cities-bad-data")
            val result = csvImport.import(true)
            StepVerifier.create(result).assertNext {
                it shouldBe ImportResult.Validated

                val errors = csvImport.validationErrors()
                errors.size shouldBe 2
                errors[1] shouldBe listOf("#/Rank: 20000 is not less or equal to 1000")
                errors[3] shouldBe listOf("#/Rank: 50000 is not less or equal to 1000")
            }.expectComplete().verify()

        }

        "read and import csv file with multiple records" {
            val result = CsvMongoDbImport("cities").import().block()!!
            result shouldBe ImportResult.Complete

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
            result shouldBe ImportResult.Complete
            result = CsvMongoDbImport("cities").import().block()!!
            result shouldBe ImportResult.Skipped
        }

        "import should execute seed when data is changed" {
            var result = CsvMongoDbImport("cities").import().block()!!
            result shouldBe ImportResult.Complete
            result = CsvMongoDbImport("cities-more").import().block()!!
            result shouldBe ImportResult.Complete
        }
    }
}