package com.github.seed


class CsvImport(private val folderName: String) {
    fun import() {
        val records = CsvReader(folderName).parse()

    }
}