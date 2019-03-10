package com.github.seed

import io.kotlintest.assertSoftly
import io.kotlintest.matchers.maps.shouldContain
import io.kotlintest.matchers.maps.shouldNotContainKey
import io.kotlintest.specs.StringSpec
import reactor.test.StepVerifier

class CsvReaderTests : StringSpec({

    "read file from cities resource folder with multiple record" {
        val records = CsvReader(Configs("cities")).parse()

        StepVerifier.create(records)
                .assertNext {
                    assertSoftly {
                        it.shouldContain("City Code", "022")
                        it.shouldContain("Display Name", "Mumbai")
                        it.shouldContain("State", "Maharashtra")
                        it.shouldContain("Rank", "100")
                    }
                }
                .assertNext {
                    assertSoftly {
                        it.shouldContain("City Code", "020")
                        it.shouldContain("Display Name", "Pune")
                        it.shouldContain("State", "Maharashtra")
                        it.shouldNotContainKey("Rank")
                    }
                }
                .expectComplete()
                .verify()

    }

})