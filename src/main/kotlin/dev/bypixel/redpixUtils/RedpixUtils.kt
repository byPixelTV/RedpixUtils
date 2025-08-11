package dev.bypixel.redpixUtils

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.github.Anon8281.universalScheduler.UniversalScheduler
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler
import dev.bypixel.redpixUtils.command.CheckCommand
import dev.bypixel.redpixUtils.command.DiscordCommand
import dev.bypixel.redpixUtils.command.RedpixUtilsCommand
import dev.bypixel.redpixUtils.command.SpawnCommand
import dev.bypixel.redpixUtils.commandWhitelist.listener.PlayerCommandPreprocessListener
import dev.bypixel.redpixUtils.commandWhitelist.listener.PlayerCommandSendListener
import dev.bypixel.redpixUtils.commandWhitelist.listener.UnknownCommandListener
import dev.bypixel.redpixUtils.listener.AsyncChatListener
import dev.bypixel.redpixUtils.listener.BlockBreakListener
import dev.bypixel.redpixUtils.listener.BlockPlaceListener
import dev.bypixel.redpixUtils.listener.ExplosionsListener
import dev.bypixel.redpixUtils.listener.CraftItemListener
import dev.bypixel.redpixUtils.listener.CrafterCraftListener
import dev.bypixel.redpixUtils.listener.EntityDamageByEntityListener
import dev.bypixel.redpixUtils.listener.PlayerDamageListener
import dev.bypixel.redpixUtils.listener.PlayerDeathListener
import dev.bypixel.redpixUtils.listener.PlayerInteractListener
import dev.bypixel.redpixUtils.listener.PlayerJoinListener
import dev.bypixel.redpixUtils.listener.PlayerMoveListener
import dev.bypixel.redpixUtils.listener.PlayerQuitListener
import dev.bypixel.redpixUtils.listener.ProjectileLaunchListener
import dev.bypixel.redpixUtils.listener.packet.RespawnPacketListener
import dev.bypixel.redpixUtils.listener.unregister
import dev.bypixel.redpixUtils.scheduler.CheckScheduler
import dev.bypixel.redpixUtils.scheduler.MaceGlowScheduler
import dev.bypixel.redpixUtils.util.CheckUtil
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin
import space.arim.libertybans.api.LibertyBans
import space.arim.omnibus.Omnibus
import space.arim.omnibus.OmnibusProvider

class RedpixUtils : JavaPlugin() {
    lateinit var scheduler: TaskScheduler
    lateinit var protocolManager: ProtocolManager

    lateinit var libertyBans: LibertyBans
    lateinit var omnibus: Omnibus

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
        CheckCommand
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

        omnibus = OmnibusProvider.getOmnibus()
        libertyBans = omnibus.registry.getProvider(LibertyBans::class.java).orElseThrow()

        AsyncChatListener
        PlayerDeathListener
        PlayerJoinListener
        PlayerQuitListener
        ExplosionsListener
        CraftItemListener
        CrafterCraftListener
        BlockBreakListener
        BlockPlaceListener
        EntityDamageByEntityListener
        PlayerDamageListener
        PlayerInteractListener
        PlayerMoveListener

        MaceGlowScheduler.start()
        CheckScheduler.start()

        protocolManager.addPacketListener(RespawnPacketListener(this))

        if (config.getBoolean("enderpearlCooldown.enabled", true)) {
            ProjectileLaunchListener
        }
    }

    override fun onDisable() {
        CheckUtil.clearChecks()

        // Plugin shutdown logic
        CommandAPI.onDisable()

        MaceGlowScheduler.stop()
        CheckScheduler.stop()

        AsyncChatListener.event.unregister()
        PlayerDeathListener.event.unregister()
        PlayerCommandSendListener.event.unregister()
        PlayerCommandPreprocessListener.event.unregister()
        UnknownCommandListener.event.unregister()
        PlayerJoinListener.event.unregister()
        ProjectileLaunchListener.event.unregister()
        PlayerQuitListener.event.unregister()
        ExplosionsListener.disableExplosionEntityEvent.unregister()
        ExplosionsListener.blockClick.unregister()
        ExplosionsListener.disableDamageEvent.unregister()
        CraftItemListener.event.unregister()
        CrafterCraftListener.event.unregister()
        BlockBreakListener.event.unregister()
        BlockPlaceListener.event.unregister()
        EntityDamageByEntityListener.event.unregister()
        PlayerDamageListener.event.unregister()
        PlayerInteractListener.event.unregister()
        PlayerMoveListener.event.unregister()
        protocolManager.removePacketListener(RespawnPacketListener(this))

        CommandAPI.unregister("discord")
        CommandAPI.unregister("dc")
        CommandAPI.unregister("redpixutils")
        CommandAPI.unregister("rputils")
        CommandAPI.unregister("rpu")
        CommandAPI.unregister("check")
        CommandAPI.unregister("spawn")
    }
}
