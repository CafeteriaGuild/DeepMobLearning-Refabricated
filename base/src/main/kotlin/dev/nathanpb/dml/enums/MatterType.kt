package dev.nathanpb.dml.enums

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.utils.getItemFromRegistry
import net.minecraft.item.Item
import net.minecraft.text.Text
import net.minecraft.util.Formatting

/** simulacrum only! */
enum class MatterType(id: String, color: Formatting) {

    OVERWORLD("overworld", Formatting.GREEN),
    HELLISH("hellish", Formatting.RED),
    EXTRATERRESTRIAL("extraterrestrial", Formatting.LIGHT_PURPLE);


    val text: Text = Text.translatable("modelType.${MOD_ID}.${id}").formatted(color)

    val matter: Item? = getItemFromRegistry(identifier("${id}_matter"))

}