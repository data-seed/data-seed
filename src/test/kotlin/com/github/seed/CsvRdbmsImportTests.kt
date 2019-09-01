package com.github.seed

import io.kotlintest.assertSoftly
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import reactor.test.StepVerifier
import java.sql.DriverManager

class CsvRdbmsImportTests : StringSpec() {

    override fun listeners(): List<TestListener> = listOf(EmbedRdbmsListener)

    init {

        "should validate successfully for correct data file for rdbms import" {
            val result = CsvRdbmsImport("cities-rdbms").import(true)
            StepVerifier.create(result).assertNext { it shouldBe ImportResult.Validated }.expectComplete().verify()
        }

        "read and import csv file with multiple records" {
            CsvRdbmsImport("cities-rdbms").import().block()!!

            val conn = DriverManager.getConnection((System.getenv("DB_URL") ?: "jdbc:h2:mem:masters"))!!
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("SELECT TYPE,UNIQUE_ID,NAME,STATE,RANK,ACTIVE FROM MASTERS;")!!
            rs.next()
            assertSoftly {
                rs.getString("TYPE") shouldBe "city"
                rs.getString("UNIQUE_ID") shouldBe "022"
                rs.getString("NAME") shouldBe "Mumbai"
                rs.getString("STATE") shouldBe "Maharashtra"
                rs.getInt("RANK") shouldBe 100
                rs.getBoolean("ACTIVE") shouldBe true
            }
            rs.next()
            assertSoftly {
                rs.getString("TYPE") shouldBe "city"
                rs.getString("UNIQUE_ID") shouldBe "020"
                rs.getString("NAME") shouldBe "Pune"
                rs.getString("STATE") shouldBe "Maharashtra"
                rs.getInt("RANK") shouldBe 0
                rs.getBoolean("ACTIVE") shouldBe true
            }
        }

        "import should return skipped on second run" {
            var result = CsvRdbmsImport("cities-rdbms").import().block()!!
            result shouldBe ImportResult.Complete
            result = CsvRdbmsImport("cities-rdbms").import().block()!!
            result shouldBe ImportResult.Skipped
        }

        "import should execute seed when data is changed" {
            var result = CsvRdbmsImport("cities-rdbms").import().block()!!
            result shouldBe ImportResult.Complete
            result = CsvRdbmsImport("cities-rdbms-more").import().block()!!
            result shouldBe ImportResult.Complete
        }

    }

}