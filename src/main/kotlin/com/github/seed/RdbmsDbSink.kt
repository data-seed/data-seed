package com.github.seed

import reactor.core.publisher.Mono
import java.sql.DriverManager
import java.sql.Timestamp
import java.time.LocalDateTime


class RdbmsDbSink(private val configs: Configs) {
    private val conn = DriverManager.getConnection((System.getenv("DB_URL") ?: "jdbc:h2:mem:masters"))!!
    private val stmt = conn.createStatement()

    fun dropData() {
        println("Running cleanup script for ${configs.seedName()}.")
        val count = stmt.executeUpdate(configs.getDropSqlQuery())
        println("$count records deleted for ${configs.seedName()}.")
    }

    fun save(sqlStmt: String): Mono<ImportResult> {
        stmt.executeUpdate(sqlStmt)
        return Mono.just(ImportResult.Success)
    }

    fun updateSeedHistory(checksum: String) {
        println("Inserting DATA_SEED_SCHEMA_HISTORY for executing seed ${configs.seedName()}.")
        createHistoryTableIfDoesNotExists()
        val sql = "INSERT INTO DATA_SEED_SCHEMA_HISTORY(SEED_NAME,CHECKSUM,SEED_TIMESTAMP) VALUES(?,?,?)"
        val prepStmt = conn.prepareStatement(sql)
        prepStmt.setString(1, configs.seedName())
        prepStmt.setString(2, checksum)
        prepStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()))
        prepStmt.execute()
    }

    private fun createHistoryTableIfDoesNotExists() {
        try {
            stmt.executeQuery("SELECT * FROM DATA_SEED_SCHEMA_HISTORY WHERE 1 = 0")
        } catch (e: Exception) {
            println("DATA_SEED_SCHEMA_HISTORY doesn't exists, creating DATA_SEED_SCHEMA_HISTORY table.")
            val createTable = "CREATE TABLE DATA_SEED_SCHEMA_HISTORY(SEED_NAME VARCHAR(200),CHECKSUM VARCHAR(200),SEED_TIMESTAMP TIMESTAMP);"
            stmt.executeUpdate(createTable)
        }
    }

    fun close() {
        println("Closing database connection for ${configs.seedName()}.")
        stmt.close()
        conn.close()
    }

    fun isSeedChanged(checksum: String): Boolean {
        createHistoryTableIfDoesNotExists()
        val query = "SELECT SEED_NAME, CHECKSUM, SEED_TIMESTAMP FROM DATA_SEED_SCHEMA_HISTORY ORDER BY SEED_TIMESTAMP DESC LIMIT 1"
        val result = stmt.executeQuery(query)
        return when {
            result?.next() == true -> result.getString("CHECKSUM") != checksum
            else -> true
        }
    }

}
