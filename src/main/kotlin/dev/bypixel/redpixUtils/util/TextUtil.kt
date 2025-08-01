package dev.bypixel.redpixUtils.util

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Color

object TextUtil {
    fun generateRandomCode(length: Int, onlyLowercase: Boolean = false): String {
        val chars = if (onlyLowercase) "abcdefghijklmnopqrstuvwxyz0123456789" else "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    fun convertMiniMessageToLegacy(miniMessage: String): String {
        // Uses Adventure API's MiniMessage and LegacyComponentSerializer to handle the transformation.
        val miniMessageParser = MiniMessage.miniMessage()
        val component = miniMessageParser.deserialize(miniMessage)
        val legacySerializer = LegacyComponentSerializer.builder()
            .character('&')
            .character('§')
            .hexColors()
            .build()
        return legacySerializer.serialize(component)
    }

    fun getHexOfMessage(string: String, hexCodeNumber: Int): String {
        val hexPattern = Regex("#[0-9a-fA-F]{6}")
        val hexCodes = hexPattern.findAll(string).map { it.value }.toList()
        return if (hexCodes.isNotEmpty()) {
            if (hexCodeNumber in 1..hexCodes.size) hexCodes[hexCodeNumber - 1] else hexCodes[0]
        } else {
            ""
        }
    }

    fun extractNumbersFromString(input: String): List<Int> {
        val regex = "\\d+".toRegex()
        return regex.findAll(input).map { it.value.toInt() }.toList()
    }

    fun extractFloatsFromString(input: String): List<Float> {
        val regex = "\\b\\d+\\.\\d+\\b".toRegex()
        return regex.findAll(input).map { it.value.toFloat() }.toList()
    }

    fun hexToColor(hex: String): Color {
        val cleanHex = hex.replace("#", "").trim()

        // Überprüfen der Eingabe
        if (!cleanHex.matches(Regex("[0-9A-Fa-f]{6}"))) {
            throw IllegalArgumentException("Ungültiger Hex-Farbcode: $hex")
        }

        // RGB-Komponenten extrahieren
        val r = cleanHex.substring(0, 2).toInt(16)
        val g = cleanHex.substring(2, 4).toInt(16)
        val b = cleanHex.substring(4, 6).toInt(16)

        // Bukkit Color erstellen
        return Color.fromRGB(r, g, b)
    }

    fun replaceWithCustomFont(input: String): String {
        val fontMap = mapOf(
            'A' to 'ᴀ', 'B' to 'ʙ', 'C' to 'ᴄ', 'D' to 'ᴅ', 'E' to 'ᴇ', 'F' to 'ꜰ', 'G' to 'ɢ', 'H' to 'ʜ', 'I' to 'ɪ', 'J' to 'ᴊ', 'K' to 'ᴋ', 'L' to 'ʟ', 'M' to 'ᴍ', 'N' to 'ɴ', 'O' to 'ᴏ', 'P' to 'ᴘ', 'Q' to 'ǫ', 'R' to 'ʀ', 'S' to 'ѕ', 'T' to 'ᴛ', 'U' to 'ᴜ', 'V' to 'ᴠ', 'W' to 'ᴡ', 'X' to 'x', 'Y' to 'ʏ', 'Z' to 'ᴢ',
            'a' to 'ᴀ', 'b' to 'ʙ', 'c' to 'ᴄ', 'd' to 'ᴅ', 'e' to 'ᴇ', 'f' to 'ꜰ', 'g' to 'ɢ', 'h' to 'ʜ', 'i' to 'ɪ', 'j' to 'ᴊ', 'k' to 'ᴋ', 'l' to 'ʟ', 'm' to 'ᴍ', 'n' to 'ɴ', 'o' to 'ᴏ', 'p' to 'ᴘ', 'q' to 'ǫ', 'r' to 'ʀ', 's' to 'ѕ', 't' to 'ᴛ', 'u' to 'ᴜ', 'v' to 'ᴠ', 'w' to 'ᴡ', 'x' to 'x', 'y' to 'ʏ', 'z' to 'ᴢ'
        )

        return input.map { char -> fontMap[char] ?: char }.joinToString("")
    }

    fun String.applyPlaceholders(placeholders: Map<String, String>): String {
        var result = this
        placeholders.forEach { (k, v) -> result = result.replace("[$k]", v) }
        return result
    }
}