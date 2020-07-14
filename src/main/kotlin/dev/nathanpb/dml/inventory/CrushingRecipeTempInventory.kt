/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.inventory

import dev.nathanpb.dml.utils.ImplementedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.DefaultedList

class CrushingRecipeTempInventory(start: ItemStack = ItemStack.EMPTY) : ImplementedInventory {
    private val _items = DefaultedList.ofSize(9, ItemStack.EMPTY)

    init {
        _items[0] = start
    }

    override fun getItems(): DefaultedList<ItemStack> = _items
}
