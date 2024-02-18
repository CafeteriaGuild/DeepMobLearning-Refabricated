package dev.nathanpb.dml.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import dev.nathanpb.dml.LOGGER
import dev.nathanpb.dml.MOD_ID
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths

fun <T : Any> initConfig(name: String, defaultConfig: T, configClass: Class<T>): T {
    val parser = JsonParser()
    val gson = GsonBuilder().setPrettyPrinting().create()

    val configFolder = "${FabricLoader.getInstance().configDir}${File.separator}$MOD_ID"
    Files.createDirectories(Paths.get(configFolder))

    val configFile = File(configFolder + "${File.separator}$name.json")
    var finalConfig: T

    LOGGER.info("Trying to read 'dml-refabricated/$name.json'...")
    try {
        if(configFile.createNewFile()) {
            LOGGER.info("Config 'dml-refabricated/$name.json' could not be found, creating a new one...")
            val json: String = gson.toJson(parser.parse(gson.toJson(defaultConfig)))
            PrintWriter(configFile).use { out -> out.println(json) }
            finalConfig = defaultConfig
            LOGGER.info("Successfully created default 'dml-refabricated/$name.json' config.")
        } else {
            LOGGER.info("'dml-refabricated/$name.json' was found, loading it..")
            finalConfig = gson.fromJson(String(Files.readAllBytes(configFile.toPath())), configClass)
            if(finalConfig == null) {
                throw NullPointerException("Config 'dml-refabricated/$name.json' was empty.")
            } else {
                LOGGER.info("Successfully loaded config 'dml-refabricated/$name.json'")
            }
        }
    } catch (exception: Exception) {
        LOGGER.error("There was an error creating/loading 'dml-refabricated/$name.json'!", exception)
        finalConfig = defaultConfig
        LOGGER.warn("Falling back to default 'dml-refabricated/$name.json'.")
    }
    return finalConfig
}