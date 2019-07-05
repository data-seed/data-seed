package com.github.seed

import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject


class RecordValidator(configs: Configs) {
    private val tobeValidated = configs.tobeValidated()
    private val schema = SchemaLoader.load(JSONObject(configs.getSchema()))

    fun validate(record: RecordMap): RecordMap {
        if (tobeValidated)  schema.validate(JSONObject(record))
        return record
    }

}
