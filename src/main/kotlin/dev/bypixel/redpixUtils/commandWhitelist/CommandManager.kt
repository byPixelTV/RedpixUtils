package dev.bypixel.redpixUtils.commandWhitelist

import dev.bypixel.redpixUtils.RedpixUtils

object CommandManager {
    fun returnWhitelist(): MutableList<String> {
        val config = RedpixUtils.instance.config

        return config.getStringList("commandWhitelist")
    }
}