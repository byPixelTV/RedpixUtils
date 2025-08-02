package dev.bypixel.redpixUtils.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import net.kyori.adventure.text.minimessage.MiniMessage

object RedpixUtilsCommand {
    init {
        commandTree("redpixutils") {
            withAliases("rputils", "rpu")
            playerExecutor { player, _ ->
                player.sendMessage(MiniMessage.miniMessage().deserialize("<aqua><click:open_url:https://bypixel.dev>RedpixUtils. Developed by byPixelTV</aqua>"))
            }
            literalArgument("reload") {
                withPermission("redpixutils.admin.reload")
                playerExecutor { player, _ ->
                    try {
                        dev.bypixel.redpixUtils.RedpixUtils.instance.reloadConfig()
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>RedpixUtils configuration reloaded successfully!</green>"))
                    } catch (e: Exception) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Error reloading RedpixUtils configuration: ${e.message}</red>"))
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}