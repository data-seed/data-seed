package com.github.seed

import com.mongodb.BasicDBObject
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.StringTemplateResolver
import java.util.*

class RecordGenerator(private val template: String) {
    private val engine: TemplateEngine = TemplateEngine()

    init {
        val resolver = StringTemplateResolver()
        resolver.templateMode = TemplateMode.TEXT
        engine.addTemplateResolver(resolver)
    }

    fun generateAsJson(record: RecordMap): RecordJson {
        return RecordJson(BasicDBObject.parse(generate(record)))
    }

    fun generate(record: RecordMap): String {
        val context = Context(Locale.getDefault(), mapOf("props" to record))
        return engine.process(template, context)
    }
}
