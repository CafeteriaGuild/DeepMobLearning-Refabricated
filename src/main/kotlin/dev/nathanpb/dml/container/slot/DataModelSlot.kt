package dev.nathanpb.dml.container.slot

import dev.nathanpb.dml.data.DataModelData
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.utils.InputRestrictedSlot
import net.minecraft.inventory.Inventory

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

enum class DataModelSlotPolicy {
    ALL,
    BOUND,
    UNBOUND;

    fun assert(data: DataModelData) = when(this) {
        BOUND -> data.isBound()
        UNBOUND -> !data.isBound()
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
