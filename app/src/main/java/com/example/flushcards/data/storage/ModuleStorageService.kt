package com.example.flushcards.data.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ModuleStorageService {

    suspend fun saveModule(context: Context, moduleId: Int, jsonContent: String): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                val file = File(context.filesDir, "module_${moduleId}.json")
                file.writeText(jsonContent)
                true
            }.getOrDefault(false)
        }
    }

    suspend fun loadModule(context: Context): List<String> {
        return withContext(Dispatchers.IO) {
            val dir = context.filesDir
            val files = dir.listFiles() ?: emptyArray()
            files.filter { file ->
                file.name.startsWith("module_") &&
                        file.name.endsWith(".json")
            }.map { file -> file.readText() }
        }
    }

    suspend fun deleteModule(context: Context, moduleId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                val file = File(context.filesDir, "module_${moduleId}.json")
                if (file.exists())
                    file.delete()
                else
                    false
            }.getOrDefault(false)
        }
    }
}