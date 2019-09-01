package com.github.seed

import reactor.core.publisher.Mono

abstract class DatabaseSink(val configs: Configs) {
    abstract fun isSeedChanged(checksum: String): Boolean
    abstract fun dropData()
    abstract fun save(record: String): Mono<ImportResult>
    abstract fun updateSeedHistory(checksum: String)
    abstract fun close()
}