/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.screen.handler.slot

import dev.nathanpb.dml.data.DataModelData
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.utils.InputRestrictedSlot
import net.minecraft.inventory.Inventory

enum class DataModelSlotPolicy {
    ALL,
    BOUND,
    UNBOUND;

    fun assert(data: DataModelData) = when(this) {
        BOUND -> data.category != null
        UNBOUND -> data.category == null
        else -> true
    }
}

class DataModelSlot (
    inventory: Inventory,
    slot: Int,
    x: Int,
    y: Int,
    policy: DataModelSlotPolicy = DataModelSlotPolicy.ALL
) : InputRestrictedSlot(inventory, slot, x, y, {
    it?.item is ItemDataModel && policy.assert(it.dataModel)
})
