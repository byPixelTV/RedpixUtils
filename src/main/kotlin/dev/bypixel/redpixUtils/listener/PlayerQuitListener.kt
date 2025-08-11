package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.command.SpawnCommand
import dev.bypixel.redpixUtils.util.ChatMessageUtil.convertToMinimessage
import dev.bypixel.redpixUtils.util.CheckUtil
import dev.bypixel.redpixUtils.util.PapiUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerQuitEvent

object PlayerQuitListener {
    val event = listen<PlayerQuitEvent> {
        val player = it.player

        it.quitMessage(Component.text(""))
        SpawnCommand.teleportingPlayers.remove(player.uniqueId)

        Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
            val chatMessage = RedpixUtils.instance.config.getString("quitMessage", "null").toString()
            onlinePlayer.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, chatMessage))))
        }

        if (CheckUtil.isInCheck(player.uniqueId) && !Bukkit.getServer().isStopping) {
            CheckUtil.removeCheck(player.uniqueId)

            CheckUtil.banPlayer(player.uniqueId)
        }
    }
}