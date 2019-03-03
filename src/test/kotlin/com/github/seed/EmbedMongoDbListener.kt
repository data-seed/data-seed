package com.github.seed

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener

object EmbedMongoDbListener : TestListener {
    private lateinit var mongodExecutable: MongodExecutable
    private lateinit var mongod: MongodProcess
    private val starter = MongodStarter.getDefaultInstance()

    override fun beforeTest(testCase: TestCase) {
        val mongodConfig = MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(Net("localhost", 27017, Network.localhostIsIPv6()))
            .build()

        mongodExecutable = starter.prepare(mongodConfig)
        mongod = mongodExecutable.start()
        Thread.sleep(2000)
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        mongod.stop()
        mongodExecutable.stop()
    }
}