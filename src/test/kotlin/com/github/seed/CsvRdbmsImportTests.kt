package com.github.seed

import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.sql.DriverManager

class CsvRdbmsImportTests : StringSpec() {


    init {
        "read and import csv file with multiple records" {

            val configs = Configs("cities-rdbms")
            val conn = DriverManager.getConnection(configs.getDatabaseUrl())!!
            val stmt = conn.createStatement()
            val createTable = "CREATE TABLE MASTERS(TYPE VARCHAR(50),UNIQUE_ID VARCHAR(100),NAME VARCHAR(100),STATE VARCHAR(100),RANK INT,ACTIVE BOOL);"
            stmt.executeUpdate(createTable)

            CsvRdbmsImport("cities-rdbms").import().blockLast()

            val rs = stmt.executeQuery("SELECT TYPE,UNIQUE_ID,NAME,STATE,RANK,ACTIVE FROM MASTERS;")!!
            rs.next()
            assertSoftly {
                rs.getString(1) shouldBe "city"
                rs.getString(2) shouldBe "022"
                rs.getString(3) shouldBe "Mumbai"
                rs.getString(4) shouldBe "Maharashtra"
                rs.getInt(5) shouldBe 100
                rs.getBoolean(6) shouldBe true
            }
            rs.next()
            assertSoftly {
                rs.getString(1) shouldBe "city"
                rs.getString(2) shouldBe "020"
                rs.getString(3) shouldBe "Pune"
                rs.getString(4) shouldBe "Maharashtra"
                rs.getInt(5) shouldBe 0
                rs.getBoolean(6) shouldBe true
            }
            stmt.close()
            conn.close()

        }
    }
}