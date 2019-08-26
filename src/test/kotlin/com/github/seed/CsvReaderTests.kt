package com.github.seed

import io.kotlintest.assertSoftly
import io.kotlintest.matchers.maps.shouldNotContainKey
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import reactor.test.StepVerifier

class CsvReaderTests : StringSpec({

    "read file from cities resource folder with multiple record" {
        val records = CsvReader(Configs("cities")).parse()

        StepVerifier.create(records)
                .assertNext {
                    assertSoftly {
                        it["City Code"] shouldBe "022"
                        it["Display Name"] shouldBe "Mumbai"
                        it["State"] shouldBe "Maharashtra"
                        it["Rank"] shouldBe 100
                    }
                }
                .assertNext {
                    assertSoftly {
                        it["City Code"] shouldBe "020"
                        it["Display Name"] shouldBe "Pune"
                        it["State"] shouldBe "Maharashtra"
                        it shouldNotContainKey "Rank"
                    }
                }
                .expectComplete()
                .verify()

    }

    "test should generate same checksum for data.csv inside cities and cities-rdbms having same content" {
        CsvReader(Configs("cities")).checksum() shouldBe "F125E78C8B254A24C830889E3AEA30A5"
        CsvReader(Configs("cities-rdbms")).checksum() shouldBe "F125E78C8B254A24C830889E3AEA30A5"
    }

    "test should generate different checksum for data.csv inside cities and cities2 having different content" {
        CsvReader(Configs("cities")).checksum() shouldBe "F125E78C8B254A24C830889E3AEA30A5"
        CsvReader(Configs("cities-more")).checksum() shouldBe "DA0226B4B2F11E051DF49C109693E9AD"
    }

})