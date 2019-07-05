package com.github.seed

import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class RecordGeneratorTests : StringSpec({

    "using Thymeleaf template generateAsJson JSON output record" {
        val generator = RecordGenerator("{ \"type\": \"city\", \"state\": \"[(\${props['State']})]\" }")
        val record = RecordMap()
        record["State"] = "Maharashtra"
        val output = generator.generateAsJson(record)
        assertSoftly {
            output["type"] shouldBe "city"
            output["state"] shouldBe "Maharashtra"
        }
    }

    "using Thymeleaf template generateAsJson SQL output record" {
        val generator = RecordGenerator("INSERT INTO CITIES VALUES(\"[(\${props['State']})]\")")
        val record = RecordMap()
        record["State"] = "Maharashtra"
        val output = generator.generate(record)
        assertSoftly {
            output shouldBe "INSERT INTO CITIES VALUES(\"Maharashtra\")"
        }
    }
})