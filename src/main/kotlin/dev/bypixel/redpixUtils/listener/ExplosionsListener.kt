package dev.bypixel.redpixUtils.listener

import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.entity.EntityType
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent

object ExplosionsListener {
    val disableDamageEvent = listen<EntityDamageByEntityEvent>(EventPriority.HIGHEST) { event ->
        val damager = event.damager
        if (damager.type == EntityType.END_CRYSTAL) {
            event.isCancelled = true
        }
    }

    val disableExplosionEntityEvent = listen<EntityExplodeEvent>(EventPriority.HIGHEST) { event ->
        val entity = event.entity
        if (entity.type == EntityType.END_CRYSTAL) {
            event.isCancelled = true
        }
    }

    val blockClick = listen<PlayerInteractEvent>(EventPriority.HIGHEST) { event ->
        val block = event.clickedBlock
        if (event.action == Action.RIGHT_CLICK_BLOCK && block?.blockData is RespawnAnchor) {
            val respawnAnchor = block.blockData as RespawnAnchor
            respawnAnchor.charges = 0
            event.isCancelled = true
        }
    }
}