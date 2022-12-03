package dev.nathanpb.dml.simulacrum.util

import dev.nathanpb.dml.simulacrum.item.*
import net.minecraft.item.Item

enum class MatterType(val matter: Item?) {

    OVERWORLD(OVERWORLD_MATTER),
    HELLISH(HELLISH_MATTER),
    EXTRATERRESTRIAL(EXTRATERRESTRIAL_MATTER);

}