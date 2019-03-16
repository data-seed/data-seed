package com.github.seed


class Configs(private val folderName: String) {
    private val config = FileResourceReader().asDocument("/$folderName/config.json")

    fun getDataFileName(): String {
        return "/$folderName/data.csv"
    }

    fun getDatabaseName(): String {
        return config.getString("databaseName")
    }

    fun getCollectionName(): String {
        return config.getString("collectionName")
    }

    fun getTemplate(): String {
        return FileResourceReader().readFileAsText("/$folderName/record.json")
    }



}