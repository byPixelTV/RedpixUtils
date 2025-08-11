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
                        groundLocation.y = targetPlayer.world.getHighestBlockYAt(groundLocation.blockX, groundLocation.blockZ).toDouble()
                        targetPlayer.teleportAsync(groundLocation, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.EntityState.RETAIN_PASSENGERS)
                        targetPlayer.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, RedpixUtils.instance.config.getString("check.startSound").toString()), Sound.Source.MASTER, 25f, 1f))
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