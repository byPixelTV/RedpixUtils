package dev.bypixel.redpixUtils.util

import dev.bypixel.redpixUtils.RedpixUtils
import space.arim.libertybans.api.PlayerVictim
import space.arim.libertybans.api.PunishmentType
import java.util.UUID

object CheckUtil {
    private val activeChecks = mutableListOf<UUID>()

    fun isInCheck(uuid: UUID): Boolean {
        return activeChecks.contains(uuid)
    }

    fun setCheck(uuid: UUID) {
        if (!activeChecks.contains(uuid)) {
            activeChecks.add(uuid)
        }
    }

    fun removeCheck(uuid: UUID) {
        activeChecks.remove(uuid)
    }

    fun clearChecks() {
        activeChecks.clear()
    }

    fun banPlayer(uuid: UUID) {
        val draftPunishment = RedpixUtils.instance.libertyBans.drafter
            .draftBuilder()
            .type(PunishmentType.BAN)
            .victim(PlayerVictim.of(uuid))
            .reason(RedpixUtils.instance.config.getString("check.banReason", "null").toString())
            .build()

        draftPunishment.enactPunishment()
    }
}