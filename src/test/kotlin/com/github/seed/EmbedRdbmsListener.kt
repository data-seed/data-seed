package com.github.seed

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import java.sql.DriverManager

object EmbedRdbmsListener : TestListener {
    private val conn = DriverManager.getConnection((System.getenv("DB_URL") ?: "jdbc:h2:mem:masters"))!!
    private val stmt = conn.createStatement()

    override fun beforeTest(testCase: TestCase) {
        val createTable = "CREATE TABLE MASTERS(TYPE VARCHAR(50),UNIQUE_ID VARCHAR(100),NAME VARCHAR(100),STATE VARCHAR(100),RANK INT,ACTIVE BOOL);"
        stmt.executeUpdate(createTable)
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        stmt.executeUpdate("DROP TABLE DATA_SEED_SCHEMA_HISTORY")
        stmt.executeUpdate("DROP TABLE MASTERS")
    }

    override fun afterSpec(spec: Spec) {
        stmt.close()
        conn.close()
    }
}