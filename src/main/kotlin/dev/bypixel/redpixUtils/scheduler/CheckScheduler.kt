package dev.bypixel.redpixUtils.scheduler

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask
import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.util.CheckUtil
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import java.time.Duration
import java.util.*

object CheckScheduler {
    private var task: MyScheduledTask? = null
    private val trackedPlayersMap = mutableMapOf<UUID, Boolean>()

    fun start() {
        if (task != null) return

        task = RedpixUtils.instance.scheduler.runTaskTimer({
            Bukkit.getOnlinePlayers().forEach { player ->
                val inCheck = CheckUtil.isInCheck(player.uniqueId)
                val wasInCheck = trackedPlayersMap[player.uniqueId] ?: false

                if (inCheck && !wasInCheck) {
                    // Check startet → Title anzeigen
                    val title = MiniMessage.miniMessage()
                        .deserialize(RedpixUtils.instance.config.getString("check.title") ?: "")
                    val subtitle = MiniMessage.miniMessage()
                        .deserialize(RedpixUtils.instance.config.getString("check.subtitle") ?: "")

                    player.showTitle(
                        Title.title(
                            title,
                            subtitle,
                            Title.Times.times(
                                Duration.ofMillis(300),  // fadeIn
                                Duration.ofSeconds(9999), // bleibt quasi unendlich
                                Duration.ofMillis(300)   // fadeOut
                            )
                        )
                    )
                }

                if (!inCheck && wasInCheck) {
                    // Check endet → Title entfernen
                    player.clearTitle()
                }

                trackedPlayersMap[player.uniqueId] = inCheck
            }
        }, 0L, 10L) // alle 0.5 Sekunden checken, nicht jeden Tick
    }

    fun stop() {
        task?.cancel()
        task = null
        trackedPlayersMap.clear()
    }
}
