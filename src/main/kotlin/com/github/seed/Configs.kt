package com.github.seed

import org.bson.Document


class Configs(val seedName: String) {
    private val config = FileResourceReader().asDocument("/$seedName/config.json")

    fun getDataFileName() = "/$seedName/data.csv"

    fun getSchema() = FileResourceReader().readFileAsText("/$seedName/schema.json")

    fun getDatabaseName() = config.getString("databaseName")!!

    fun getCollectionName() = config.getString("collectionName")!!

    fun getJsonTemplate() = FileResourceReader().readFileAsText("/$seedName/record.json")
    fun getSqlTemplate() = FileResourceReader().readFileAsText("/$seedName/record.sql")

    fun getDropQuery() = config["dropQuery"] as Document

    fun isCleanupRequired() = config["seedMode"] == "drop-insert"

    fun tobeValidated() = config["schemaValidation"] == true

    fun getDatabaseUrl() = config.getString("connectionUrl")!!
    fun getDropSqlQuery() = config.getString("dropQuery")!!

}