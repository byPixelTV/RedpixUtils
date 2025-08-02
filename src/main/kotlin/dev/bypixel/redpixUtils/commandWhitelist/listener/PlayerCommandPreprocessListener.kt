package dev.bypixel.redpixUtils.commandWhitelist.listener

import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.commandWhitelist.CommandManager
import dev.bypixel.redpixUtils.listener.listen
import dev.bypixel.redpixUtils.util.ChatMessageUtil.convertToMinimessage
import dev.bypixel.redpixUtils.util.PapiUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object PlayerCommandPreprocessListener : Listener {

    val event = listen<PlayerCommandPreprocessEvent>(EventPriority.MONITOR) { event ->
        val commandWhitelist = CommandManager.returnWhitelist()
        // Check if plugin is still enabled
        if (!RedpixUtils.instance.isEnabled) return@listen

        if (event.player.isOp || event.player.hasPermission("redpixutils.commandwhitelist.bypass")) return@listen

        val command = event.message.split(" ")[0].removePrefix("/").lowercase()
        if (!commandWhitelist.contains(command)) {
            val message = RedpixUtils.instance.config.getString("commandNotFoundMsg", "null").toString()
                .replace("%command%", command)
            event.player.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(event.player, message))))
            event.isCancelled = true
        }
    }
}