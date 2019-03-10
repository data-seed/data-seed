package com.github.seed


class DataRecord {

    fun build(columns: Array<String>, values: Array<String>): RecordMap {
        val data = RecordMap()
        columns.forEachIndexed { index, name ->
            val value = values[index].trim()
            if (!value.isBlank()) {
                data[name.trim()] = value
            }
        }
        return data
    }

}
