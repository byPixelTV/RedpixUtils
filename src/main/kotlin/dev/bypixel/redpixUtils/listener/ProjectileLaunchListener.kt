package dev.bypixel.redpixUtils.listener

import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.util.ChatMessageUtil.convertToMinimessage
import dev.bypixel.redpixUtils.util.PapiUtil
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import java.util.UUID

object ProjectileLaunchListener {
    private val playerCooldowns = mutableMapOf<UUID, Long>()

    val event = listen<ProjectileLaunchEvent> {
        val player = it.entity.shooter ?: return@listen
        val projectile = it.entity
        if (player !is Player) return@listen

        if (projectile.type != EntityType.ENDER_PEARL) return@listen

        val cooldown = RedpixUtils.instance.config.getInt("enderpearlCooldown.cooldown", 30) * 1000L // Convert seconds to milliseconds
        val currentTime = System.currentTimeMillis()

        if (playerCooldowns[player.uniqueId] != null) {
            val lastLaunchTime = playerCooldowns[player.uniqueId]!!
            if (lastLaunchTime > currentTime) {
                it.isCancelled = true
                val message = RedpixUtils.instance.config.getString("enderpearlCooldown.cooldownMessage", "null").toString()
                    .replace("%seconds%", ((lastLaunchTime - currentTime) / 1000L).toString())
                player.sendMessage(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, message))))
                player.sendActionBar(MiniMessage.miniMessage().deserialize(convertToMinimessage(PapiUtil.parsePlaceholders(player, message))))
                player.playSound(Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.MASTER, 100f, 1f))
                return@listen
            } else {
                // Update the last launch time
                playerCooldowns[player.uniqueId] = currentTime + cooldown
            }
        } else {
            // First launch, set the current time
            playerCooldowns[player.uniqueId] = currentTime + cooldown
        }
    }
}