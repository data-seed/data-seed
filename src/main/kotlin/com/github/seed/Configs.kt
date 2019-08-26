package com.github.seed

import org.bson.Document


class Configs(private val seedFolder: String) {
    private val config = FileResourceReader("/$seedFolder/config.json").asDocument()

    fun seedName()  = config.getString("seedName") ?: seedFolder
    fun getDataFileName() = "/$seedFolder/data.csv"

    fun tobeValidated() = config["schemaValidation"] == true
    fun getSchema() = FileResourceReader("/$seedFolder/schema.json").readFileAsText()

    fun getDatabaseName() = config.getString("databaseName")!!
    fun getCollectionName() = config.getString("collectionName")!!

    fun getJsonTemplate() = FileResourceReader("/$seedFolder/record.json").readFileAsText()
    fun getSqlTemplate() = FileResourceReader("/$seedFolder/record.sql").readFileAsText()

    fun isCleanupRequired() = config["seedMode"] == "drop-insert"
    fun getDropQuery() = config["dropQuery"] as Document
    fun getDropSqlQuery() = config.getString("dropQuery")!!

}