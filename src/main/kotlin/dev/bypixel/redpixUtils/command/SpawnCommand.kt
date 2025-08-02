package dev.bypixel.redpixUtils.command

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask
import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.util.ChatMessageUtil.convertToMinimessage
import dev.bypixel.redpixUtils.util.PapiUtil
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.papermc.paper.entity.TeleportFlag
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.UUID

object SpawnCommand {
    val teleportingPlayers = mutableListOf<UUID>()

    init {
        commandTree("spawn") {
            playerExecutor { player, _ ->
                if (player.uniqueId in teleportingPlayers) {
                    val message = RedpixUtils.instance.config.getString("spawn.alreadyTeleportingMessage", "null").toString()
                    player.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, message))))
                    player.playSound(Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.MASTER, 100f, 1f))
                    return@playerExecutor
                }

                val world = RedpixUtils.instance.config.getString("spawn.location.world") ?: "world"
                val x = RedpixUtils.instance.config.getDouble("spawn.location.x")
                val y = RedpixUtils.instance.config.getDouble("spawn.location.y")
                val z = RedpixUtils.instance.config.getDouble("spawn.location.z")
                val yaw = RedpixUtils.instance.config.getDouble("spawn.location.yaw")
                val pitch = RedpixUtils.instance.config.getDouble("spawn.location.pitch")
                var countdownSeconds = RedpixUtils.instance.config.getInt("spawn.countdown", 5)

                val spawnLocation = Location(
                    RedpixUtils.instance.server.getWorld(world) ?: RedpixUtils.instance.server.worlds[0],
                    x, y, z, yaw.toFloat(), pitch.toFloat()
                )

                val oldPlayerXYZ = Triple(player.location.x, player.location.y, player.location.z)

                var task: MyScheduledTask? = null

                teleportingPlayers.add(player.uniqueId)
                val chatMessage = RedpixUtils.instance.config.getString("spawn.countdownTeleportStartMessage", "null").toString()
                    .replace("%seconds%", countdownSeconds.toString())
                player.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, chatMessage))))
                countdownSeconds = RedpixUtils.instance.config.getInt("spawn.countdown", 5) +1
                task = RedpixUtils.instance.scheduler.runTaskTimer({
                    countdownSeconds--
                    if (oldPlayerXYZ != Triple(player.location.x, player.location.y, player.location.z)) {
                        teleportingPlayers.remove(player.uniqueId)
                        val chatMessage = RedpixUtils.instance.config.getString("spawn.countdownFailedMessage", "null").toString()
                        player.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, chatMessage))))
                        RedpixUtils.instance.scheduler.runTaskLater({
                            val actionbarMessage = RedpixUtils.instance.config.getString("spawn.countdownFailedMessageActionbar", "null").toString()
                            player.sendActionBar(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, actionbarMessage))))
                        }, 1L)
                        player.playSound(Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.MASTER, 100f, 1f))
                        task?.cancel()
                        task = null
                    }
                    if (countdownSeconds <= 0) {
                        teleportingPlayers.remove(player.uniqueId)
                        player.teleportAsync(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS).thenAccept {
                            if (it) {
                                val chatMessage = RedpixUtils.instance.config.getString("spawn.countdownSuccessMessage", "null").toString()
                                player.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, chatMessage))))
                                val actionbarMessage = RedpixUtils.instance.config.getString("spawn.countdownSuccessMessageActionbar", "null").toString()
                                player.sendActionBar(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, actionbarMessage))))
                                player.playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.MASTER, 100f, 1f))
                            }
                        }
                        task?.cancel()
                        task = null
                    } else {
                        val actionbarMessage = RedpixUtils.instance.config.getString("spawn.countdownMessage", "null").toString()
                            .replace("%seconds%", countdownSeconds.toString())
                        player.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 100f, 1f))
                        player.sendActionBar(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, actionbarMessage))))
                    }
                }, 0L, 20L)
            }
        }
    }
}