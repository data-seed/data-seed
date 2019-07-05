package com.github.seed

import org.bson.Document


class Configs(private val folderName: String) {
    private val config = FileResourceReader().asDocument("/$folderName/config.json")

    fun getDataFileName() = "/$folderName/data.csv"

    fun getSchema() = FileResourceReader().readFileAsText("/$folderName/schema.json")

    fun getDatabaseName() = config.getString("databaseName")!!

    fun getCollectionName() = config.getString("collectionName")!!

    fun getTemplate() = FileResourceReader().readFileAsText("/$folderName/record.json")

    fun getDropQuery() = config["dropQuery"] as Document

    fun isCleanupRequired() = config["seedMode"] == "drop-insert" && getDropQuery().isNotEmpty()

    fun tobeValidated() = config["schemaValidation"] == true

}