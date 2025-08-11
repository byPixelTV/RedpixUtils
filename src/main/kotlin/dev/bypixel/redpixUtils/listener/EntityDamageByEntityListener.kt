package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.util.CheckUtil
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

object EntityDamageByEntityListener {
    val event = listen<EntityDamageByEntityEvent> {
        val damager = it.damager

        if (damager !is Player) return@listen

        if (CheckUtil.isInCheck(damager.uniqueId)) {
            it.isCancelled = true
        }
    }
}