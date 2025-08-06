package dev.bypixel.redpixUtils

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.github.Anon8281.universalScheduler.UniversalScheduler
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler
import dev.bypixel.redpixUtils.command.DiscordCommand
import dev.bypixel.redpixUtils.command.RedpixUtilsCommand
import dev.bypixel.redpixUtils.command.SpawnCommand
import dev.bypixel.redpixUtils.commandWhitelist.listener.PlayerCommandPreprocessListener
import dev.bypixel.redpixUtils.commandWhitelist.listener.PlayerCommandSendListener
import dev.bypixel.redpixUtils.commandWhitelist.listener.UnknownCommandListener
import dev.bypixel.redpixUtils.listener.AsyncChatListener
import dev.bypixel.redpixUtils.listener.PlayerDeathListener
import dev.bypixel.redpixUtils.listener.PlayerJoinListener
import dev.bypixel.redpixUtils.listener.PlayerQuitListener
import dev.bypixel.redpixUtils.listener.ProjectileLaunchListener
import dev.bypixel.redpixUtils.listener.packet.RespawnPacketListener
import dev.bypixel.redpixUtils.listener.unregister
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin

class RedpixUtils : JavaPlugin() {
    lateinit var scheduler: TaskScheduler
    lateinit var protocolManager: ProtocolManager

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

        saveDefaultConfig()

        DiscordCommand
        RedpixUtilsCommand

        if (config.getBoolean("spawn.enabled", true)) {
            // Register the spawn command if enabled
            SpawnCommand
        }
    }

    override fun onEnable() {
        scheduler = UniversalScheduler.getScheduler(this)

        // Plugin startup logic
        CommandAPI.onEnable()
        protocolManager = ProtocolLibrary.getProtocolManager()

        AsyncChatListener
        PlayerDeathListener
        PlayerJoinListener
        PlayerQuitListener

        protocolManager.addPacketListener(RespawnPacketListener(this))

        if (config.getBoolean("enderpearlCooldown.enabled", true)) {
            ProjectileLaunchListener
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
        CommandAPI.onDisable()

        AsyncChatListener.event.unregister()
        PlayerDeathListener.event.unregister()
        PlayerCommandSendListener.event.unregister()
        PlayerCommandPreprocessListener.event.unregister()
        UnknownCommandListener.event.unregister()
        PlayerJoinListener.event.unregister()
        ProjectileLaunchListener.event.unregister()
        PlayerQuitListener.event.unregister()
        protocolManager.removePacketListener(RespawnPacketListener(this))

        CommandAPI.unregister("discord")
        CommandAPI.unregister("dc")
        CommandAPI.unregister("redpixutils")
        CommandAPI.unregister("rputils")
        CommandAPI.unregister("rpu")
        CommandAPI.unregister("spawn")
    }
}
