package dev.bypixel.redpixUtils.util

import dev.bypixel.redpixUtils.RedpixUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.regex.Pattern

object ChatMessageUtil {
    private val mm = MiniMessage.miniMessage()

    fun normalizeMessage(message: String): String {
        val normalizedMessage = message.replace("\\s+".toRegex(), "").replace("-", "").replace("\\\\", "")
        return PlainTextComponentSerializer.plainText().serialize(mm.deserialize(normalizedMessage))
    }

    fun highlightOnlinePlayerNames(message: String): String {
        var result = message
        val onlinePlayers = Bukkit.getOnlinePlayers()

        for (onlinePlayer in onlinePlayers) {
            val playerName = onlinePlayer.name

            val pattern = Pattern.compile("\\b$playerName\\b", Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(result)

            val builder = StringBuffer()
            while (matcher.find()) {
                matcher.appendReplacement(builder, "<u>$playerName</u>")
            }
            matcher.appendTail(builder)

            result = builder.toString()
        }

        return result
    }

    fun formatMessage(player: Player, message: String): Component {
        val configMessage = RedpixUtils.instance.config.getString("chat-format")!!

        return if (player.hasPermission("redpixutils.chat.color")) {
            mm.deserialize(PapiUtil.parsePlaceholders(player, convertToMinimessage(configMessage).replace("%message%", convertToMinimessage(message))))
        } else {
            val filteredMessage = message.replace(Regex("<(?!/?u\\b)[^>]*>"), "")
            mm.deserialize(PapiUtil.parsePlaceholders(player, "<grey>${convertToMinimessage(configMessage).replace("%message%", filteredMessage)}</grey>"))
        }
    }

    fun convertToMinimessage(input: String): String {
        val legacySerializer = LegacyComponentSerializer.builder()
            .character('&')
            .extractUrls()
            .hexColors()
            .build()

        // Deserialize the legacy formatted string to a Component
        val component = legacySerializer.deserialize(input.replace("§", "&"))

        // Serialize the Component to a MiniMessage formatted string
        val miniMessageString = MiniMessage.miniMessage().serialize(component)

        return miniMessageString.replace("\\", "")
    }

    /**
     * Prüft, ob ein Text Unicode-Zeichen (außerhalb des grundlegenden ASCII-Bereichs, aber deutsche Umlaute erlaubt)
     * oder potenzielle benutzerdefinierte Schriftzeichen enthält.
     *
     * @param text Der zu prüfende Text
     * @return true, wenn der Text Unicode (außer deutsche Umlaute) oder potenzielle benutzerdefinierte Schriftzeichen enthält
     */
    fun containsUnicodeOrCustomFont(text: String): Boolean {
        // Erlaubte deutsche Sonderzeichen
        val allowed = setOf('ä', 'ö', 'ü', 'Ä', 'Ö', 'Ü', 'ß')

        // Prüft auf Unicode-Zeichen (außerhalb des ASCII-Bereichs 0-127), aber erlaubt deutsche Umlaute
        for (c in text) {
            if (c.code > 127 && c !in allowed) {
                return true
            }
        }

        // Prüft auf Minecraft-spezifische benutzerdefinierte Schriftzeichen
        val customFontPatterns = arrayOf(
            "\\\\u[fF][0-9a-fA-F]{3}", // Pattern für \uf000 bis \uffff
            "§[k-oK-O]"                // Minecraft-Formatierungscodes für spezielle Texteffekte
        )

        for (pattern in customFontPatterns) {
            if (text.contains(Regex(pattern))) {
                return true
            }
        }

        return false
    }

    fun isInAlphaNumeric(text: String): Boolean {
        // Prüft, ob der Text nur alphanumerische Zeichen (Buchstaben und Ziffern) enthält
        return text.all { it.isLetterOrDigit() }
    }

    fun convertToPlaintext(string: String): String {
        // Konvertiert einen MiniMessage-String in einen einfachen Text
        return PlainTextComponentSerializer.plainText().serialize(mm.deserialize(convertToMinimessage(string)))
    }

    fun convertComponentToPlaintext(component: Component): String {
        // Konvertiert einen Component in einen einfachen Text
        return PlainTextComponentSerializer.plainText().serialize(component)
    }
}