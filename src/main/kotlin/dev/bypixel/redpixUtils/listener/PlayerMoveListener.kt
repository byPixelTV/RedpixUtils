package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.util.CheckUtil
import org.bukkit.event.player.PlayerMoveEvent

object PlayerMoveListener {
    val event = listen<PlayerMoveEvent> {
        val player = it.player

        if (CheckUtil.isInCheck(player.uniqueId)) {
            it.isCancelled = true
        }
    }
}