package dev.bypixel.redpixUtils.commandWhitelist.listener

import dev.bypixel.redpixUtils.RedpixUtils
import dev.bypixel.redpixUtils.commandWhitelist.CommandManager
import dev.bypixel.redpixUtils.listener.listen
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandSendEvent

object PlayerCommandSendListener : Listener {

    val event = listen<PlayerCommandSendEvent>(EventPriority.LOWEST) { event ->
        // Plugin-Status prüfen
        if (!RedpixUtils.instance.isEnabled) return@listen

        // Spieler-Status prüfen
        if (!event.player.isOnline) {
            event.commands.clear()
            return@listen
        }

        // Bypass für Operatoren und Administratoren
        if (event.player.isOp || event.player.hasPermission("redpixutils.commandwhitelist.bypass")) {
            return@listen
        }

        val commandWhitelist = CommandManager.returnWhitelist()

        // Sichere Kopie erstellen, um ConcurrentModification zu vermeiden
        val commandsToRemove = event.commands.filterNot { commandName ->
            // Normalisierte Version des Befehls für die Überprüfung
            val normalizedCommand = commandName.removePrefix("/").split(":").last().lowercase()

            // Prüfen ob der Befehl in irgendeiner Form in der Whitelist ist
            commandWhitelist.contains(normalizedCommand) ||
                    commandWhitelist.contains(commandName) ||
                    commandWhitelist.contains(commandName.lowercase())
        }

        // Alle nicht erlaubten Befehle entfernen
        event.commands.removeAll(commandsToRemove)
    }
}