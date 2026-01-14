package com.thedevjade.typingwithlux.dialogue.entries

import com.typewritermc.engine.paper.entry.descendants
import com.typewritermc.engine.paper.entry.entity.SimpleEntityDefinition
import com.typewritermc.entity.entries.entity.custom.NpcDefinition
import com.typewritermc.entity.entries.entity.custom.NpcInstance
import com.typewritermc.engine.paper.extensions.placeholderapi.parsePlaceholders
import org.aselstudios.luxdialoguesapi.Builders.Dialogue
import org.aselstudios.luxdialoguesapi.LuxDialoguesAPI
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object LuxDialogueSharedData {
    val playerDialogues = ConcurrentHashMap<String, SharedDialogueData>()
    private val lastDialogueEnd = mutableMapOf<String, Long>()
    private const val COOLDOWN_MS = 300L

    fun isInDialogue(player: Player): Boolean = LuxDialoguesAPI.getProvider().isInDialogue(player)

    fun isInCommandCooldown(player: Player): Boolean = LuxDialoguesAPI.getProvider().isInCommandCooldown(player)

    fun clearDialogue(player: Player) = LuxDialoguesAPI.getProvider().clearDialogue(player)

    fun hasActiveDialogue(player: Player): Boolean {
        val playerId = player.uniqueId.toString()
        return playerDialogues.containsKey(playerId)
    }

    fun generateUniqueDialogueId(baseId: String): String {
        val numericPart = baseId.takeWhile { it.isDigit() }
        return if (numericPart.isNotEmpty()) numericPart
        else "$baseId-${UUID.randomUUID().toString().take(8)}"
    }

    fun wasDialogueRecentlyEnded(player: Player): Boolean {
        val playerId = player.uniqueId.toString()
        val lastEnd = lastDialogueEnd[playerId] ?: return false
        val elapsed = System.currentTimeMillis() - lastEnd
        if (elapsed >= COOLDOWN_MS) {
            lastDialogueEnd.remove(playerId)
            return false
        }
        return true
    }

    fun markDialogueEnd(player: Player) {
        lastDialogueEnd[player.uniqueId.toString()] = System.currentTimeMillis()
    }

    data class SharedDialogueData(
        var dialogue: Dialogue,
        var pageId: String,
        var entryId: String,
        var baseEntryId: String
    )

    fun sendDialogue(player: Player, dialogue: Dialogue, pageId: String, baseEntryId: String) {
        val playerId = player.uniqueId.toString()
        playerDialogues[playerId] = SharedDialogueData(dialogue, pageId, dialogue.dialogueID, baseEntryId)
        LuxDialoguesAPI.getProvider().sendDialogue(player, dialogue, pageId)
    }

    fun redirectDialogue(player: Player, dialogue: Dialogue, pageId: String, baseEntryId: String) {
        val playerId = player.uniqueId.toString()
        playerDialogues[playerId] = SharedDialogueData(dialogue, pageId, dialogue.dialogueID, baseEntryId)
        LuxDialoguesAPI.getProvider().redirectDialogue(player, dialogue, pageId)
    }

    fun getBaseEntryId(player: Player): String? {
        val playerId = player.uniqueId.toString()
        return playerDialogues[playerId]?.baseEntryId
    }

    fun getCurrentEntryId(player: Player): String? {
        val playerId = player.uniqueId.toString()
        return playerDialogues[playerId]?.entryId
    }

    fun clearPlayerDialogue(player: Player) {
        val playerId = player.uniqueId.toString()
        playerDialogues.remove(playerId)
    }

    fun clearAllPlayerData(player: Player) {
        val playerId = player.uniqueId.toString()
        playerDialogues.remove(playerId)
        lastDialogueEnd.remove(playerId)
    }

    fun clearAll() {
        playerDialogues.clear()
        lastDialogueEnd.clear()
    }
}

private fun extractDataFrom(speaker: Any?): LuxNpcData? {
    val dataWithEntry = when (speaker) {
        is NpcDefinition -> speaker.data
        is NpcInstance -> speaker.definition.get()?.data
        is SimpleEntityDefinition -> speaker.data
        else -> null
    } ?: return null

    return dataWithEntry.descendants(LuxNpcData::class).firstOrNull()?.get()
}

fun Any?.extractLuxNpcData(): LuxNpcData? = extractDataFrom(this)

fun Dialogue.Builder.applyLuxNpcData(data: LuxNpcData, dialogueSpeed: Int = 1, player: Player? = null): Dialogue.Builder {
    return this
        .setTypingSound(data.typingSound, data.typingSoundCategory, data.typingSoundVolume, data.typingSoundPitch)
        .setSelectionSound(data.selectionSound, data.selectionSoundCategory, data.selectionSoundVolume, data.selectionSoundPitch)
        .setAnswerNumbers(false)
        .setArrowImage(data.arrowImage, data.arrowColor, data.arrowOffset)
        .setDialogueBackgroundImage(data.dialogueBackgroundImage, data.dialogueBackgroundColor, data.dialogueBackgroundOffset)
        .setAnswerBackgroundImage(data.answerBackgroundImage, data.answerBackgroundColor, data.answerBackgroundOffset)
        .setDialogueText(data.dialogueTextColor, data.dialogueTextOffset)
        .setAnswerText(data.answerTextColor, data.answerTextOffset, data.answerSelectedColor)
        .setCharacterImage(data.imageName, data.characterColor, data.characterOffset)
        .setCharacterNameText(data.characterName.let { if (player != null) it.parsePlaceholders(player) else it }, data.characterNameColor, data.characterNameOffset)
        .setNameImage(data.nameStartImage, data.nameMidImage, data.nameEndImage, data.nameBackgroundColor, data.nameImageOffset)
        .setFogImage(data.fogImage, data.fogColor)
        .setEffect(data.effect)
        .setPreventExit(data.preventExit)
        .setDialogueSpeed(dialogueSpeed)
}
