package com.github.seed


class DataRecord(private val index: Int) {
    val regex = """\[(.*?)\]$""".toRegex()

    fun build(columns: Array<String>, values: Array<String>): RecordMap {
        val data = RecordMap()
        data["_index"] = index
        columns.forEachIndexed { index, actualColumnName ->
            val value = values[index].trim()
            val column = actualColumnName.trim()
            if (!value.isBlank()) {
                data[clean(column)] = convertType(column, value)
            }
        }
        return data
    }

    private fun clean(column: String): String {
        return regex.replace(column, "")
    }

    private fun convertType(column: String, value: String): Any {
        val result = regex.find(column) ?: return value
        return when (result.value) {
            "[Int]", "[int]", "[integer]", "[Integer]" -> value.toInt()
            "[Number]" -> value.toFloat()
            "[Bool]", "[bool]", "[Boolean]", "[boolean]" -> value.toBoolean()
            else -> value
        }
    }

}
