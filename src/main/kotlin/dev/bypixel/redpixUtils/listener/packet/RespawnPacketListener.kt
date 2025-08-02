package dev.bypixel.redpixUtils.listener.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import dev.bypixel.redpixUtils.RedpixUtils
import io.papermc.paper.entity.TeleportFlag
import org.bukkit.Location
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.Plugin

class RespawnPacketListener(plugin: Plugin) : PacketAdapter(
    plugin,
    ListenerPriority.NORMAL,
    PacketType.Play.Server.RESPAWN
) {
    override fun onPacketSending(event: PacketEvent) {
        val player = event.player

        if (RedpixUtils.instance.config.getBoolean("spawn.teleportOnDeath")) {
            try {
                if (player.hasMetadata("redpixutils:deathflag")) {
                    player.removeMetadata("redpixutils:deathflag", RedpixUtils.instance)
                    if (player.respawnLocation == null) {
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

                        val isTeleported = player.teleportAsync(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS)
                        isTeleported.thenAccept {
                            if (it) {
                                player.spigot().respawn()
                            }
                        }
                    } else {
                        val respawnLocation = player.respawnLocation!!
                        val isTeleported = player.teleportAsync(respawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS)
                        isTeleported.thenAccept {
                            if (it) {
                                player.spigot().respawn()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}