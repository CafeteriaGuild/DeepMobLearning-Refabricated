package dev.nathanpb.dml.item

import dev.nathanpb.dml.identifier
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

val ITEM_GROUP = FabricItemGroupBuilder.build(identifier("tab_deepmoblearning")) {
    ItemStack(ITEM_DML)
}

private fun settings(baseSettings: Item.Settings = Item.Settings()) = baseSettings.apply {
    group(ITEM_GROUP)
}

val ITEM_DML = Item(Item.Settings())


fun registerItems() {
    mapOf(
        ITEM_DML to "deepmoblearning"
    ).forEach { (item, id) ->
        Registry.register(Registry.ITEM, identifier(id), item)
    }
}
