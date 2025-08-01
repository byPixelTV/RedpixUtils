package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.util.ChatMessageUtil
import dev.bypixel.redpixUtils.util.ChatMessageUtil.highlightOnlinePlayerNames
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventPriority

object AsyncChatListener {
    private val mm = MiniMessage.miniMessage()

    val event = listen<AsyncChatEvent>(EventPriority.NORMAL) { event ->
        val player = event.player

        if (event.isCancelled) return@listen

        val messageComponent = event.message()
        val message = mm.serialize(messageComponent)
            .replace("\\\\([\\[\\]<>])".toRegex(), "$1")

        val messageWithHighlightedNames = highlightOnlinePlayerNames(message)

        val formattedMessage = ChatMessageUtil.formatMessage(player, messageWithHighlightedNames)

        // Set the renderer of the AsyncChatEvent
        event.renderer { _, _, _, _ -> formattedMessage }
    }
}