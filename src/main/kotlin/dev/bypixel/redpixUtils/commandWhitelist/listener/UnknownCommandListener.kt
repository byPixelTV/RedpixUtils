package dev.bypixel.redpixUtils.commandWhitelist.listener

import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.listener.listen
import dev.bypixel.redpixUtils.util.ChatMessageUtil.convertToMinimessage
import dev.bypixel.redpixUtils.util.PapiUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.command.UnknownCommandEvent

object UnknownCommandListener : Listener {

    val event = listen<UnknownCommandEvent>(EventPriority.MONITOR) { event ->
        if (!RedpixUtils.instance.isEnabled) return@listen
        if (event.sender !is Player) return@listen

        val commandName = event.commandLine.split(" ")[0]
        if (Bukkit.getCommandMap().getCommand(commandName) == null) {
            val message = RedpixUtils.instance.config.getString("commandNotFoundMsg", "null").toString()
                .replace("%command%", commandName)
            event.sender.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(event.sender as Player, message))))
        }
    }
}