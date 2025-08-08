package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.RedpixUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.CraftItemEvent

object CraftItemListener {
    val event = listen<CraftItemEvent> { event ->
        if (RedpixUtils.instance.config.getBoolean("allowMaceCrafting", true)) return@listen

        if (event.whoClicked !is Player) return@listen

        val craftedItem = event.currentItem ?: return@listen
        val craftedItemType = craftedItem.type
        if (craftedItemType == Material.MACE) {
            event.isCancelled = true
        }
    }
}