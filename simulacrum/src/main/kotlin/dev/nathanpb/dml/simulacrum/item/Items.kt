package dev.nathanpb.dml.simulacrum.item

import dev.nathanpb.dml.config
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.itemgroup.ITEM_GROUP_KEY
import dev.nathanpb.dml.simulacrum.SIMULATION_CHAMBER
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

val OVERWORLD_MATTER = ItemMatter(FabricItemSettings(), config.matterXP.overworldMatterXP)
val HELLISH_MATTER = ItemMatter(FabricItemSettings(), config.matterXP.hellishMatterXP)
val EXTRATERRESTRIAL_MATTER = ItemMatter(FabricItemSettings(), config.matterXP.extraterrestrialMatterXP)
val POLYMER_CLAY = Item(FabricItemSettings())

fun registerItems() {
    linkedMapOf(
        EXTRATERRESTRIAL_MATTER to "extraterrestrial_matter",
        HELLISH_MATTER to "hellish_matter",
        OVERWORLD_MATTER to "overworld_matter",
        POLYMER_CLAY to "polymer_clay"
    ).forEach { (item, id) ->
        Registry.register(Registries.ITEM, identifier(id), item)
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register {
            it.addAfter(ItemStack(SIMULATION_CHAMBER), item)
        }
    }
}