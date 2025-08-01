package dev.bypixel.redpixUtils

import dev.bypixel.redpixUtils.command.DiscordCommand
import dev.bypixel.redpixUtils.listener.AsyncChatListener
import dev.bypixel.redpixUtils.listener.PlayerDeathListener
import dev.bypixel.redpixUtils.listener.unregister
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin

class RedpixUtils : JavaPlugin() {
    companion object {
        lateinit var instance: RedpixUtils
            private set
    }

    init {
        instance = this
    }

    override fun onLoad() {
        // Plugin load logic
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(false).setNamespace("redpixutils").beLenientForMinorVersions(true))

        DiscordCommand
    }

    override fun onEnable() {
        // Plugin startup logic
        CommandAPI.onEnable()

        saveDefaultConfig()

        AsyncChatListener
        PlayerDeathListener
    }

    override fun onDisable() {
        // Plugin shutdown logic
        CommandAPI.onDisable()

        AsyncChatListener.event.unregister()
        PlayerDeathListener.event.unregister()

        CommandAPI.unregister("discord")
        CommandAPI.unregister("dc")
    }
}
