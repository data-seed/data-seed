package com.github.seed

import reactor.core.publisher.Mono


abstract class CsvImport(folderName: String) {
    protected val configs = Configs(folderName)
    private val reader = CsvReader(configs)
    private val validator = RecordValidator(configs)
    private val generator = RecordGenerator(configs.getDbTemplate())
    protected lateinit var dbSink: DatabaseSink

    fun validationErrors() = validator.validationErrors()

    fun import(dryRun: Boolean = (System.getenv("DRY_RUN") ?: "false").toBoolean()): Mono<ImportResult> {
        return when {
            dryRun -> {
                println("Running just validation for ${configs.seedName()}.")
                reader.parse()
                        .map { validator.validate(it) }
                        .doOnError { println(it.message) }
                        .then(Mono.just(ImportResult.Validated))
            }
            dbSink.isSeedChanged(reader.checksum()) -> {
                println("Changes detected for ${configs.seedName()}, executing seed.")
                if (configs.isCleanupRequired()) dbSink.dropData()
                reader.parse()
                        .map { validator.validate(it) }
                        .map { generator.generate(it) }
                        .concatMap { dbSink.save(it) }
                        .doOnComplete { dbSink.updateSeedHistory(reader.checksum()) }
                        .doFinally { dbSink.close() }
                        .doOnError { println(it.message) }
                        .then(Mono.just(ImportResult.Complete))
            }
            else -> {
                println("No changes detected for ${configs.seedName()}, skipping seed.")
                Mono.just(ImportResult.Skipped)
            }
        }
    }
}