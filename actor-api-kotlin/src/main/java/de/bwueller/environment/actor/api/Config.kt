package de.bwueller.environment.actor.api

import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.util.*

data class Config(val name: String, val processorAddress: String) {

  companion object {

    fun load(): Config {
      val resourceName = "config.properties"
      val loader = Thread.currentThread().contextClassLoader
      val props = Properties()
      loader.getResourceAsStream(resourceName).use { resourceStream -> props.load(resourceStream) }

      val configFile = File(props["config_file_location"] as String)

      return if (!configFile.exists()) {
        val config = Config(UUID.randomUUID().toString(), "ws://localhost:24499")

        configFile.createNewFile()
        Files.write(configFile.toPath(), Gson().toJson(config).toByteArray())

        config
      } else {
        Gson().fromJson<Config>(FileReader(configFile), Config::class.java)
      }
    }
  }
}
