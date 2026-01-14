package com.thedevjade.typingwithlux

import com.thedevjade.typingwithlux.dialogue.entries.LuxDialogueEvents
import com.thedevjade.typingwithlux.dialogue.entries.LuxDialogueSharedData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class Entry : JavaPlugin(), Listener {

    override fun onEnable() {
        server.pluginManager.registerEvents(LuxDialogueEvents(), this)
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        server.onlinePlayers.forEach { player ->
            LuxDialogueSharedData.clearAllPlayerData(player)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(e: PlayerQuitEvent) {
        LuxDialogueSharedData.clearAllPlayerData(e.player)
    }
}
