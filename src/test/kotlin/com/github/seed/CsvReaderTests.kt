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
                        it["Rank"] shouldBe "100"
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

})