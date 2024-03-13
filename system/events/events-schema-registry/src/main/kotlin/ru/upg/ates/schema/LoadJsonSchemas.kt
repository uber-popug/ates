package ru.upg.ates.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SchemaId
import com.networknt.schema.SpecVersion
import java.io.File
import java.net.URI

/**
 * Assuming there is single root directory which could contain infinite level of
 * other directories which contains JSON Schema files in YAML format
 */
class LoadJsonSchemas(private val path: String) {
    fun execute(): Map<String, JsonSchema> {
        val factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4)
        val metaSchema = factory.getSchema(URI.create(SchemaId.V4))
        return File(path)
            .walk(FileWalkDirection.TOP_DOWN)
            .filter { it.isFile && it.extension == "yaml" }
            .map { it.path to it.readText() }
            .filter { it.second.isNotEmpty() }
            .map { (path, content) -> processSchema(metaSchema, path, content) }
            .map(factory::getSchema)
            .map { it.id to it }
            .toMap()
    }

    private fun processSchema(
        metaSchema: JsonSchema,
        schemaPath: String,
        schemaContent: String
    ): JsonNode {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.readTree(schemaContent).also { targetSchema ->
            val messages = metaSchema.validate(targetSchema)
            if (messages.isNotEmpty()) {
                val msg = messages.joinToString { it.message }
                throw IllegalStateException(
                    "There are exceptions to validate event " +
                        "schema on path $schemaPath with message '$msg'"
                )
            }
        }
    }
}
