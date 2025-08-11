package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.util.CheckUtil
import org.bukkit.event.block.BlockPlaceEvent

object BlockPlaceListener {
    val event = listen<BlockPlaceEvent> {
        val player = it.player

        if (CheckUtil.isInCheck(player.uniqueId)) {
            it.isCancelled = true
        }
    }
}