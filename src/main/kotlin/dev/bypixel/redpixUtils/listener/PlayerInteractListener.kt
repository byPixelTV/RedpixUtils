package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.util.CheckUtil
import org.bukkit.event.player.PlayerInteractEvent

object PlayerInteractListener {
    val event = listen<PlayerInteractEvent> {
        val player = it.player

        if (CheckUtil.isInCheck(player.uniqueId)) {
            it.isCancelled = true
        }
    }
}