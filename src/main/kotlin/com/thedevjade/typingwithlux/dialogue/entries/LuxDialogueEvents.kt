package com.thedevjade.typingwithlux.dialogue.entries

import com.typewritermc.engine.paper.logger
import org.aselstudios.luxdialoguesapi.Events.DialogueAnsweredEvent
import org.aselstudios.luxdialoguesapi.Events.DialogueStartEvent
import org.aselstudios.luxdialoguesapi.Events.DialogueStopEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class LuxDialogueEvents : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onDialogueStart(e: DialogueStartEvent) {
        val player = e.player
        logger.info("[LuxDialogueEvents] Dialogue started for ${player.name}: ${e.dialogue.dialogueID}")
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onDialogueStop(e: DialogueStopEvent) {
        logger.info("[LuxDialogueEvents] Dialogue stopped for ${e.player.name}: ${e.dialogue.dialogueID}")
        LuxDialogueSharedData.markDialogueEnd(e.player)
        LuxDialogueSharedData.clearPlayerDialogue(e.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onDialogueAnswered(e: DialogueAnsweredEvent) {
        val player = e.player
        logger.info("[LuxDialogueEvents] Dialogue answered: ${e.answer.answerID}")
    }
}
