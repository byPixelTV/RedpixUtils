package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.util.CheckUtil
import org.bukkit.event.block.BlockBreakEvent

object BlockBreakListener {
    val event = listen<BlockBreakEvent> {
        val player = it.player

        if (CheckUtil.isInCheck(player.uniqueId)) {
            it.isCancelled = true
        }
    }
}