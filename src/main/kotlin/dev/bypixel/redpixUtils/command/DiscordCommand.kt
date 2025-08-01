package dev.bypixel.redpixUtils.command

import dev.bypixel.redpixUtils.RedpixUtils
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import net.kyori.adventure.text.minimessage.MiniMessage

object DiscordCommand {
    init {
        commandTree("discord") {
            withAliases("dc")
            playerExecutor { player, arguments ->
                player.sendMessage(MiniMessage.miniMessage().deserialize(RedpixUtils.instance.config.getString("discord-message", "null") ?: "null"))
            }
        }
    }
}