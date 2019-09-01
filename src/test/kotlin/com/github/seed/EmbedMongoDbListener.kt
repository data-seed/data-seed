package com.github.seed

import com.mongodb.reactivestreams.client.MongoClients
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import reactor.core.publisher.toMono

object EmbedMongoDbListener : TestListener {
    private lateinit var mongodExecutable: MongodExecutable
    private lateinit var mongod: MongodProcess
    private val starter = MongodStarter.getDefaultInstance()

    override fun beforeSpec(spec: Spec) {
        val mongodConfig = MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net("localhost", 27017, Network.localhostIsIPv6()))
                .build()

        mongodExecutable = starter.prepare(mongodConfig)
        mongod = mongodExecutable.start()
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        val client = MongoClients.create("mongodb://localhost:27017")
        val database = client.getDatabase("masters")
        database.getCollection("masters").drop().toMono().block()
        database.getCollection("dataSeedSchemaHistory").drop().toMono().block()
    }

    override fun afterSpec(spec: Spec) {
        mongod.stop()
        mongodExecutable.stop()
    }
}