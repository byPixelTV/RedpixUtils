package dev.bypixel.redpixUtils.scheduler

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask
import dev.bypixel.redpixUtils.RedpixUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID

object MaceGlowScheduler {
    private var task: MyScheduledTask? = null
    private val trackedPlayersMap = mutableMapOf<UUID, Boolean>()

    fun start() {
        if (task != null) return

        task = RedpixUtils.instance.scheduler.runTaskTimerAsynchronously({
            Bukkit.getOnlinePlayers().forEach { player ->
                // Prüfe ob Spieler das Item besitzt
                if (player.inventory.contains(ItemStack.of(Material.MACE))) {
                    if (trackedPlayersMap[player.uniqueId] != true) {
                        trackedPlayersMap[player.uniqueId] = true
                        RedpixUtils.instance.scheduler.runTask(player) {
                            player.addPotionEffect(
                                PotionEffect(
                                    PotionEffectType.GLOWING,
                                    -1,
                                    1,
                                    false,
                                    false,
                                    true
                                )
                            )
                        }
                    }
                } else {
                    // Wenn der Spieler keinen MACE hat, Effekt entfernen, falls vorhanden
                    if (trackedPlayersMap[player.uniqueId] == true) {
                        trackedPlayersMap.remove(player.uniqueId)
                        RedpixUtils.instance.scheduler.runTask(player) {
                            player.removePotionEffect(PotionEffectType.GLOWING)
                        }
                    }
                }
            }
        }, 0L, 5 * 20L)
    }

    fun stop() {
        task?.cancel()
        task = null
        trackedPlayersMap.clear()
    }
}