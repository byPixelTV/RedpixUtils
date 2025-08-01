package dev.bypixel.redpixUtils.util

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import java.util.regex.Matcher
import java.util.regex.Pattern

object PapiUtil {
    fun getPlaceholder(player: Player, placeholder: String): String {
        return PlaceholderAPI.setPlaceholders(player, placeholder)
    }

    fun parsePlaceholders(player: Player, text: String): String {
        val pattern = Pattern.compile("%([^%]+)%")
        val matcher = pattern.matcher(text)
        val result = StringBuffer()
        while (matcher.find()) {
            val placeholder = matcher.group()
            val replaced = PlaceholderAPI.setPlaceholders(player, placeholder)
            matcher.appendReplacement(result, Matcher.quoteReplacement(replaced))
        }
        matcher.appendTail(result)
        return result.toString()
    }
}