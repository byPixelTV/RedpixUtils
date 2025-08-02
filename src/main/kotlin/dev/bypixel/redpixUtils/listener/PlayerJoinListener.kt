package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.command.SpawnCommand
import dev.bypixel.redpixUtils.util.ChatMessageUtil.convertToMinimessage
import dev.bypixel.redpixUtils.util.PapiUtil
import io.papermc.paper.entity.TeleportFlag
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent

object PlayerJoinListener {
    val event = listen<PlayerJoinEvent> {
        val player = it.player
        val config = RedpixUtils.instance.config

        it.joinMessage(Component.text(""))
        SpawnCommand.teleportingPlayers.remove(player.uniqueId)

        val world = RedpixUtils.instance.config.getString("spawn.location.world") ?: "world"
        val x = RedpixUtils.instance.config.getDouble("spawn.location.x")
        val y = RedpixUtils.instance.config.getDouble("spawn.location.y")
        val z = RedpixUtils.instance.config.getDouble("spawn.location.z")
        val yaw = RedpixUtils.instance.config.getDouble("spawn.location.yaw")
        val pitch = RedpixUtils.instance.config.getDouble("spawn.location.pitch")

        val spawnLocation = Location(
            RedpixUtils.instance.server.getWorld(world) ?: RedpixUtils.instance.server.worlds[0],
            x, y, z, yaw.toFloat(), pitch.toFloat()
        )

        val isUniqueJoin = !player.hasPlayedBefore()

        if (isUniqueJoin) {
            Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
                val chatMessage = RedpixUtils.instance.config.getString("uniqueJoinMessage", "null").toString()
                onlinePlayer.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.firework_rocket.twinkle"), Sound.Source.MASTER, 25f, 1f))
                onlinePlayer.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, chatMessage))))
            }

            if (config.getBoolean("spawn.teleportOnFirstJoin")) {
                player.teleportAsync(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS)
            }

        } else {
            Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
                val chatMessage = RedpixUtils.instance.config.getString("joinMessage", "null").toString()
                onlinePlayer.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, chatMessage))))
            }
            if (config.getBoolean("spawn.teleportOnJoin")) {
                player.teleportAsync(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS)
            }
        }
    }
}