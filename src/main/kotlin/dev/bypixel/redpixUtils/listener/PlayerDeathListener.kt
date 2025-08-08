package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.RedpixUtils
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.metadata.FixedMetadataValue

object PlayerDeathListener {
    val event = listen<PlayerDeathEvent> {
        val player = it.entity
//        val killer = it.entity.killer

        player.setMetadata("redpixutils:deathflag", FixedMetadataValue(RedpixUtils.instance, true))

//        it.deathMessage(Component.text(""))
//
//        if (killer != null) {
//            val deathMessage = MiniMessage.miniMessage().deserialize(
//                convertToMinimessage(RedpixUtils.instance.config.getString("kill-message", null) ?: "null")
//                    .replace("%killer%", killer.name)
//                    .replace("%victim%", player.name)
//            )
//            it.deathMessage(deathMessage)
//        }
    }
}