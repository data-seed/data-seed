package com.github.seed


class DataRecord {

    fun build(columns: Array<String>, values: Array<String>): Map<String, Any> {
        val data = mutableMapOf<String, Any>()
        columns.forEachIndexed { index, name ->
            val value = values[index].trim()
            if (!value.isBlank()) {
                data[name.trim()] = value
            }
        }
        return data
    }

}
