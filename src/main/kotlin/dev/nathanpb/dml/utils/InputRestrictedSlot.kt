package dev.nathanpb.dml.utils;

import net.minecraft.container.Slot
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

class InputRestrictedSlot(
    inventory: Inventory,
    slot: Int,
    x: Int,
    y: Int,
    private val accept: (ItemStack?)->Boolean
) : Slot(inventory, slot, x, y) {
    override fun canInsert(stack: ItemStack?) = accept(stack)
}
