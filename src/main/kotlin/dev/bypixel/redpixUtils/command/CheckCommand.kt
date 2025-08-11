package dev.bypixel.redpixUtils.command

import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.util.CheckUtil
import dev.bypixel.redpixUtils.util.PapiUtil
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.papermc.paper.entity.TeleportFlag
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

object CheckCommand {
    init {
        commandTree("check") {
            withPermission("redpixutils.staff.check.use")
            entitySelectorArgumentOnePlayer("player") {
                playerExecutor { player, arguments ->
                    val targetPlayer = arguments[0] as Player

                    if (CheckUtil.isInCheck(targetPlayer.uniqueId)) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(RedpixUtils.instance.config.getString("check.staffInfoAlreadyChecked").toString()))
                    } else {
                        CheckUtil.setCheck(targetPlayer.uniqueId)
                        player.sendMessage(MiniMessage.miniMessage().deserialize(PapiUtil.parsePlaceholders(targetPlayer, RedpixUtils.instance.config.getString("check.staffInfoStart").toString())))
                        targetPlayer.sendMessage(MiniMessage.miniMessage().deserialize(RedpixUtils.instance.config.getString("check.startMessage").toString()))

                        val groundLocation = targetPlayer.location.clone()

                        RedpixUtils.instance.scheduler.runTask(groundLocation) {
                            val world = targetPlayer.world
                            val startY = groundLocation.blockY

                            // Prüfen ob unter dem Spieler Luft ist
                            if (!world.getBlockAt(groundLocation.blockX, startY - 1, groundLocation.blockZ).type.isSolid) {
                                var y = startY

                                // Nach unten scannen bis wir auf einen soliden Block treffen
                                for (currentY in startY downTo world.minHeight) {
                                    val block = world.getBlockAt(groundLocation.blockX, currentY, groundLocation.blockZ)
                                    if (block.type.isSolid || block.type == Material.WATER || block.type == Material.LAVA) {
                                        y = currentY + 1 // +1 = auf den Block drauf, nicht rein
                                        break
                                    }
                                }

                                groundLocation.y = y.toDouble()

                                targetPlayer.teleportAsync(
                                    groundLocation,
                                    PlayerTeleportEvent.TeleportCause.PLUGIN,
                                    TeleportFlag.EntityState.RETAIN_PASSENGERS
                                )
                            }

                            targetPlayer.playSound(
                                Sound.sound(
                                    Key.key(Key.MINECRAFT_NAMESPACE, RedpixUtils.instance.config.getString("check.startSound").toString()),
                                    Sound.Source.MASTER,
                                    25f,
                                    1f
                                )
                            )
                        }
                    }
                }
                literalArgument("pass") {
                    playerExecutor { player, arguments ->
                        val targetPlayer = arguments[0] as Player

                        if (CheckUtil.isInCheck(targetPlayer.uniqueId)) {
                            CheckUtil.removeCheck(targetPlayer.uniqueId)
                            player.sendMessage(MiniMessage.miniMessage().deserialize(PapiUtil.parsePlaceholders(targetPlayer, RedpixUtils.instance.config.getString("check.staffInfoPass").toString())))
                            targetPlayer.sendMessage(MiniMessage.miniMessage().deserialize(RedpixUtils.instance.config.getString("check.passMessage").toString()))
                            targetPlayer.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, RedpixUtils.instance.config.getString("check.passSound").toString()), Sound.Source.MASTER, 25f, 1f))
                        } else {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(PapiUtil.parsePlaceholders(targetPlayer, RedpixUtils.instance.config.getString("check.staffInfoNotChecked").toString())))
                        }
                    }
                }
                literalArgument("deny") {
                    playerExecutor { player, arguments ->
                        val targetPlayer = arguments[0] as Player

                        if (CheckUtil.isInCheck(targetPlayer.uniqueId)) {
                            CheckUtil.removeCheck(targetPlayer.uniqueId)
                            player.sendMessage(MiniMessage.miniMessage().deserialize(PapiUtil.parsePlaceholders(targetPlayer, RedpixUtils.instance.config.getString("check.staffInfoFail").toString())))
                            CheckUtil.banPlayer(targetPlayer.uniqueId)
                        } else {
                            player.sendMessage(MiniMessage.miniMessage().deserialize(PapiUtil.parsePlaceholders(targetPlayer, RedpixUtils.instance.config.getString("check.staffInfoNotChecked").toString())))
                        }
                    }
                }
            }
        }
    }
}