package dev.nathanpb.dml.item

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


val ITEM_GROUP = FabricItemGroupBuilder.build(identifier("tab_deepmoblearning")) {
    ItemStack(ITEM_DML)
}

fun settings(baseSettings: Item.Settings = Item.Settings()) = baseSettings.apply {
    group(ITEM_GROUP)
}

val ITEM_DML = Item(Item.Settings())
val ITEM_DEEP_LEARNER = ItemDeepLearner()
val ITEM_DATA_MODEL = ItemDataModel()
val ITEM_TRIAL_KEY = ItemTrialKey()


fun registerItems() {
    mapOf(
        ITEM_DML to "deepmoblearning",
        ITEM_DEEP_LEARNER to "deep_learner",
        ITEM_DATA_MODEL to "data_model",
        ITEM_TRIAL_KEY to "trial_key"
    ).forEach { (item, id) ->
        Registry.register(Registry.ITEM, identifier(id), item)
    }
}
