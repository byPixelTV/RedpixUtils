package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.util.CheckUtil
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent

object PlayerDamageListener {
    val event = listen<EntityDamageEvent> {
        val entity = it.entity

        if (entity !is Player) return@listen

        if (CheckUtil.isInCheck(entity.uniqueId)) {
            it.isCancelled = true
        }
    }
}