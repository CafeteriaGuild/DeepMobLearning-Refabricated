package dev.nathanpb.dml.itemgroup

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ITEM_DML
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text


val ITEM_GROUP_KEY: RegistryKey<ItemGroup> = RegistryKey.of(RegistryKeys.ITEM_GROUP, identifier("tab_${MOD_ID}"))
val ITEMS = ArrayList<ItemStack>()

fun registerItemGroup() {
    Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_KEY, FabricItemGroup.builder()
        .displayName(Text.translatable("itemGroup.$MOD_ID.tab_$MOD_ID"))
        .icon { ItemStack(ITEM_DML) }
        .entries { _: ItemGroup.DisplayContext, entries: ItemGroup.Entries ->
            entries.addAll(ITEMS)
        }
    .build())
}