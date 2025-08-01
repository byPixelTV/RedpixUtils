package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.util.ChatMessageUtil.convertToMinimessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.entity.PlayerDeathEvent

object PlayerDeathListener {
    val event = listen<PlayerDeathEvent> {
        val player = it.entity
        val killer = it.entity.killer

        it.deathMessage(Component.text(""))

        if (killer != null) {
            val deathMessage = MiniMessage.miniMessage().deserialize(
                convertToMinimessage(RedpixUtils.instance.config.getString("kill-message", null) ?: "null")
                    .replace("%killer%", killer.name)
                    .replace("%victim%", player.name)
            )
            it.deathMessage(deathMessage)
        }
    }
}