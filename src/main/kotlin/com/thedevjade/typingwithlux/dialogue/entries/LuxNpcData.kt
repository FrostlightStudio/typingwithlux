package com.thedevjade.typingwithlux.dialogue.entries

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.core.extension.annotations.Placeholder
import com.typewritermc.core.extension.annotations.Tags
import com.typewritermc.engine.paper.entry.entity.SinglePropertyCollectorSupplier
import com.typewritermc.engine.paper.entry.entries.EntityProperty
import com.typewritermc.engine.paper.entry.entries.GenericEntityData
import org.bukkit.entity.Player
import java.util.*
import kotlin.reflect.KClass

@Entry("lux_npc_data", "Data regarding what LuxDialogues should use", Colors.RED, "mdi:marker")
@Tags("lux_npc_data", "lux_npc")
class LuxNpcData(
    override val id: String = "",
    override val name: String = "",
    @Help("The name of the NPC")
    @Placeholder
    val characterName: String = "",
    @Help("Dialogue background image name")
    val dialogueBackgroundImage: String = "dialogue-background",
    @Help("Answer background image name")
    val answerBackgroundImage: String = "answer_3_3",
    override val priorityOverride: Optional<Int> = Optional.empty(),
) : GenericEntityData<LuxNpcProperty> {
    override fun type(): KClass<LuxNpcProperty> = LuxNpcProperty::class

    override fun build(player: Player): LuxNpcProperty = LuxNpcProperty(
        characterName,
        dialogueBackgroundImage,
        answerBackgroundImage
    )
}

data class LuxNpcProperty(
    val characterName: String,
    val dialogueBackgroundImage: String,
    val answerBackgroundImage: String,
) : EntityProperty {
    companion object : SinglePropertyCollectorSupplier<LuxNpcProperty>(LuxNpcProperty::class)
}
