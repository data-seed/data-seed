package com.github.seed

import com.mongodb.reactivestreams.client.Success
import reactor.core.publisher.Mono
import java.sql.DriverManager



class RdbmsDbSink(private val configs: Configs) {
    private val conn = DriverManager.getConnection(configs.getDatabaseUrl())!!
    private val stmt = conn.createStatement()

    init {
        if (configs.isCleanupRequired()) {
            println ("Running cleanup script for ${configs.seedName}.")
            val count = stmt.executeUpdate(configs.getDropSqlQuery())
            println("$count records deleted for ${configs.seedName}.")
        }
    }

    fun save(insertStmt: String) : Mono<Success> {
        stmt.executeUpdate(insertStmt)
        return Mono.just(Success.SUCCESS)
    }

    fun close() {
        println ("Closing database connection for ${configs.seedName}.")
        stmt.close()
        conn.close()
    }

}
