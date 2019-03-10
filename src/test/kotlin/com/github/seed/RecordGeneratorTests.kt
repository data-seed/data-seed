package com.github.seed

import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class RecordGeneratorTests : StringSpec({

    "using Thymeleaf template generate output record" {
        val generator = RecordGenerator("{ \"type\": \"city\", \"state\": \"[(\${props['State']})]\" }")
        val record = RecordMap()
        record["State"] = "Maharashtra"
        val output = generator.generate(record)
        assertSoftly {
            output.getString("type") shouldBe "city"
            output.getString("state") shouldBe "Maharashtra"
        }
    }

})