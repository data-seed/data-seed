package com.github.seed

import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject


class RecordValidator(val configs: Configs) {
    private val validationErrors = mutableMapOf<Int, List<String>>()
    private val tobeValidated = configs.tobeValidated()
    private val schema = SchemaLoader.load(JSONObject(configs.getSchema()))

    fun validationErrors() = validationErrors.toMap()
    fun isErrors() = validationErrors.isNotEmpty()

    fun validate(record: RecordMap): RecordMap {
        if (tobeValidated) {
            try {
                schema.validate(JSONObject(record))
            } catch (e: ValidationException) {
                print("Validation errors for seed '${configs.seedName()}' at index #${record["_index"]} : ${e.allMessages}")
                validationErrors[record["_index"] as Int] = e.allMessages
            }
        }
        return record
    }

}
