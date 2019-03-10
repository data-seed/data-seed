package com.github.seed

import com.mongodb.BasicDBObject
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.StringTemplateResolver

class RecordGenerator(private val jsonTemplate: String) {
    private val engine: TemplateEngine = TemplateEngine()

    init {
        val resolver = StringTemplateResolver()
        resolver.templateMode = TemplateMode.TEXT
        engine.addTemplateResolver(resolver)
    }

    fun generate(record: RecordMap): RecordJson {
        val context = Context()
        context.setVariable("props", record)
        val generatedString = engine.process(jsonTemplate, context)
        val recordMap = BasicDBObject.parse(generatedString)
        return RecordJson(recordMap)
    }

}
