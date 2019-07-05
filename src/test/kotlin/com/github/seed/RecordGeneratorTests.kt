package com.github.seed

import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class RecordGeneratorTests : StringSpec({

    "using Thymeleaf template generateAsBson JSON output record" {
        val generator = RecordGenerator("{ \"type\": \"city\", \"state\": \"[(\${props['State']})]\" }")
        val record = RecordMap()
        record["State"] = "Maharashtra"
        val output = generator.generateAsBson(record)
        assertSoftly {
            output["type"] shouldBe "city"
            output["state"] shouldBe "Maharashtra"
        }
    }

    "using Thymeleaf template generateAsBson SQL output record" {
        val generator = RecordGenerator("INSERT INTO CITIES VALUES(\"[(\${props['State']})]\")")
        val record = RecordMap()
        record["State"] = "Maharashtra"
        val output = generator.generate(record)
        assertSoftly {
            output shouldBe "INSERT INTO CITIES VALUES(\"Maharashtra\")"
        }
    }
})