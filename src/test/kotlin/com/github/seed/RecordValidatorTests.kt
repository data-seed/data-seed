package com.github.seed

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class RecordValidatorTests : StringSpec({

    "should validate record with zero errors" {
        val columns = arrayOf("City Code", "Display Name", "State", "Rank[Int]")
        val values = arrayOf("022", "Mumbai", "Maharastra", "100")
        val record = DataRecord(1).build(columns, values)

        val configs = Configs("cities")
        val validator = RecordValidator(configs)
        validator.validate(record) shouldBe true

        validator.validationErrors().size shouldBe 0
    }

    "invalid record, multiple validation failed for a record" {
        val columns = arrayOf("City Code", "Display Name", "State", "Rank[Int]")
        val values = arrayOf("02A", "Mumbai", "Maharastra", "2000")
        val record = DataRecord(5).build(columns, values)

        val configs = Configs("cities")
        val validator = RecordValidator(configs)

        validator.validate(record) shouldBe false
        val errors = validator.validationErrors()
        errors.size shouldBe 1
        errors[5]?.size shouldBe 2
        errors[5] shouldBe listOf("#/City Code: string [02A] does not match pattern ^\\d{0,4}\$",
                "#/Rank: 2000 is not less or equal to 1000")
    }

    "should skip validation when overridden as false" {
        val columns = arrayOf("City Code", "Display Name", "State", "Rank[Int]")
        val values = arrayOf("02A", "Mumbai", "Maharastra", "2000")
        val record = DataRecord(1).build(columns, values)

        val configs = Configs("cities")
        val validator = RecordValidator(configs)
        validator.validate(record, false) shouldBe true

        validator.validationErrors().size shouldBe 0
    }


})