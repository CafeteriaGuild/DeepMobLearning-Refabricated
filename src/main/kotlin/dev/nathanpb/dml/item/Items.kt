package dev.nathanpb.dml.item

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.data.EntityCategory
import dev.nathanpb.dml.identifier
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */


val ITEM_GROUP = FabricItemGroupBuilder.build(identifier("tab_${MOD_ID}")) {
    ItemStack(ITEM_DML)
}

fun settings(baseSettings: Item.Settings = Item.Settings()) = baseSettings.apply {
    group(ITEM_GROUP)
}

val ITEM_DML = Item(Item.Settings())
val ITEM_DEEP_LEARNER = ItemDeepLearner()
val ITEM_TRIAL_KEY = ItemTrialKey()

val ITEM_DATA_MODEL = ItemDataModel()
val ITEM_DATA_MODEL_NETHER = ItemDataModel(EntityCategory.NETHER)
val ITEM_DATA_MODEL_SLIMY = ItemDataModel(EntityCategory.SLIMY)
val ITEM_DATA_MODEL_OVERWORLD = ItemDataModel(EntityCategory.OVERWORLD)
val ITEM_DATA_MODEL_ZOMBIE = ItemDataModel(EntityCategory.ZOMBIE)
val ITEM_DATA_MODEL_SKELETON = ItemDataModel(EntityCategory.SKELETON)
val ITEM_DATA_MODEL_END = ItemDataModel(EntityCategory.END)
val ITEM_DATA_MODEL_GHOST = ItemDataModel(EntityCategory.GHOST)
val ITEM_DATA_MODEL_ILLAGER = ItemDataModel(EntityCategory.ILLAGER)
val ITEM_DATA_MODEL_OCEAN = ItemDataModel(EntityCategory.OCEAN)

val ITEM_PRISTINE_MATTER_NETHER = ItemPristineMatter(settings(), EntityCategory.NETHER)
val ITEM_PRISTINE_MATTER_SLIMY = ItemPristineMatter(settings(), EntityCategory.SLIMY)
val ITEM_PRISTINE_MATTER_OVERWORLD = ItemPristineMatter(settings(), EntityCategory.OVERWORLD)
val ITEM_PRISTINE_MATTER_ZOMBIE = ItemPristineMatter(settings(), EntityCategory.ZOMBIE)
val ITEM_PRISTINE_MATTER_SKELETON = ItemPristineMatter(settings(), EntityCategory.SKELETON)
val ITEM_PRISTINE_MATTER_END = ItemPristineMatter(settings(), EntityCategory.END)
val ITEM_PRISTINE_MATTER_GHOST = ItemPristineMatter(settings(), EntityCategory.GHOST)
val ITEM_PRISTINE_MATTER_ILLAGER = ItemPristineMatter(settings(), EntityCategory.ILLAGER)
val ITEM_PRISTINE_MATTER_OCEAN = ItemPristineMatter(settings(), EntityCategory.OCEAN)

val ITEM_SOOT_REDSTONE = Item(settings())
val ITEM_SOOT_PLATE = Item(settings())
val ITEM_SOOT_MACHINE_CASE = Item(settings())


fun registerItems() {
    mapOf(
        ITEM_DML to MOD_ID,
        ITEM_DEEP_LEARNER to "deep_learner",
        ITEM_TRIAL_KEY to "trial_key",
        ITEM_DATA_MODEL to "data_model",
        ITEM_DATA_MODEL_NETHER to "data_model_nether",
        ITEM_DATA_MODEL_SLIMY to "data_model_slimy",
        ITEM_DATA_MODEL_OVERWORLD to "data_model_overworld",
        ITEM_DATA_MODEL_ZOMBIE to "data_model_zombie",
        ITEM_DATA_MODEL_SKELETON to "data_model_skeleton",
        ITEM_DATA_MODEL_END to "data_model_end",
        ITEM_DATA_MODEL_GHOST to "data_model_ghost",
        ITEM_DATA_MODEL_ILLAGER to "data_model_illager",
        ITEM_DATA_MODEL_OCEAN to "data_model_ocean",
        ITEM_SOOT_REDSTONE to "soot_redstone",
        ITEM_SOOT_PLATE to "soot_plate",
        ITEM_SOOT_MACHINE_CASE to "machine_casing",
        ITEM_PRISTINE_MATTER_NETHER to "pristine_matter_nether",
        ITEM_PRISTINE_MATTER_SLIMY to "pristine_matter_slimy",
        ITEM_PRISTINE_MATTER_OVERWORLD to "pristine_matter_overworld",
        ITEM_PRISTINE_MATTER_ZOMBIE to "pristine_matter_zombie",
        ITEM_PRISTINE_MATTER_SKELETON to "pristine_matter_skeleton",
        ITEM_PRISTINE_MATTER_END to "pristine_matter_end",
        ITEM_PRISTINE_MATTER_GHOST to "pristine_matter_ghost",
        ITEM_PRISTINE_MATTER_ILLAGER to "pristine_matter_illager",
        ITEM_PRISTINE_MATTER_OCEAN to "pristine_matter_ocean"
    ).forEach { (item, id) ->
        Registry.register(Registry.ITEM, identifier(id), item)
    }
}
