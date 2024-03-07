package ru.upg.ates.schema

import java.io.File

/**
 * Assuming there is single root directory which could contain infinite level of
 * other directories which contains JSON Schema files in YAML format
 */
class LoadJsonSchemas(private val path: String) {
    fun execute(): Map<String, String> {
        return File(path)
            .walk(FileWalkDirection.TOP_DOWN)
            .filter { it.isFile && it.extension == "yaml" }
            .map { it.nameWithoutExtension to it.readText() }
            .toMap()
    }
}
