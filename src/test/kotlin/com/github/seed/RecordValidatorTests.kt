package com.github.seed

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.everit.json.schema.ValidationException

class RecordValidatorTests : StringSpec({

    "valid record" {

        val columns = arrayOf("City Code","Display Name","State","Rank[Int]")
        val values = arrayOf("022","Mumbai","Maharastra","100")
        val record = DataRecord().build(columns,values)

        val configs = Configs("cities")
        val validator = RecordValidator(configs)
        val validateRecord = validator.validate(record)
        validateRecord shouldBe record
    }

    "invalid record" {
        val columns = arrayOf("City Code","Display Name","State","Rank[Int]")
        val values = arrayOf("022","Mumbai","Maharastra","2000")
        val record = DataRecord().build(columns,values)

        val configs = Configs("cities")
        val validator = RecordValidator(configs)

        val exception = shouldThrow<ValidationException> {
            validator.validate(record)
        }
        exception.allMessages shouldBe listOf("#/Rank: 2000 is not less or equal to 1000")
    }
})