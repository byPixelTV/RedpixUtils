package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.RedpixUtils
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.metadata.FixedMetadataValue

object PlayerDeathListener {
    val event = listen<PlayerDeathEvent> {
        val player = it.entity

        player.setMetadata("redpixutils:deathflag", FixedMetadataValue(RedpixUtils.instance, true))

        // Blitz-Effekt an Todesposition ohne Schaden/Itemzerstörung
        RedpixUtils.instance.scheduler.runTask(player.location) {
            player.world.strikeLightningEffect(player.location)
        }
    }
}