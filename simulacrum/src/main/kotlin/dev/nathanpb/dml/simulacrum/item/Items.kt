package dev.nathanpb.dml.simulacrum.item

import dev.nathanpb.dml.config
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ITEM_GROUP
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

val POLYMER_CLAY = Item(FabricItemSettings().maxCount(64).group(ITEM_GROUP))
val OVERWORLD_MATTER = ItemMatter(FabricItemSettings().maxCount(64).group(ITEM_GROUP), config.matterXP.overworldMatterXP)
val HELLISH_MATTER = ItemMatter(FabricItemSettings().maxCount(64).group(ITEM_GROUP),  config.matterXP.hellishMatterXP)
val EXTRATERRESTRIAL_MATTER = ItemMatter(FabricItemSettings().maxCount(64).group(ITEM_GROUP),  config.matterXP.extraterrestrialMatterXP)

fun registerItems() {
    mapOf(
        POLYMER_CLAY to "polymer_clay",
        OVERWORLD_MATTER to "overworld_matter",
        HELLISH_MATTER to "hellish_matter",
        EXTRATERRESTRIAL_MATTER to "extraterrestrial_matter"
    ).forEach { (item, id) ->
        Registry.register(Registry.ITEM, identifier(id), item)
    }
}