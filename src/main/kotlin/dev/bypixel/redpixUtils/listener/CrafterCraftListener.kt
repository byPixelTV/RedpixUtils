package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.RedpixUtils
import org.bukkit.Material
import org.bukkit.event.block.CrafterCraftEvent

object CrafterCraftListener {
    val event = listen<CrafterCraftEvent> {
        if (RedpixUtils.instance.config.getBoolean("allowMaceCrafting", true)) return@listen

        if (it.result.type == Material.MACE) {
            it.isCancelled = true
        }
    }
}