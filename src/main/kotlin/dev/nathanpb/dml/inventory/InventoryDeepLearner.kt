package dev.nathanpb.dml.inventory

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

import dev.nathanpb.dml.container.ContainerDeepLearner
import dev.nathanpb.dml.item.deepLearnerInventory
import dev.nathanpb.dml.utils.ImplementedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.DefaultedList

class InventoryDeepLearner(val container: ContainerDeepLearner) : ImplementedInventory {

    private val _items = DefaultedList.ofSize(container.stack.deepLearnerInventory.size, ItemStack.EMPTY)

    override fun getItems() = _items

    override fun takeInvStack(slot: Int, count: Int): ItemStack {
        return super.takeInvStack(slot, count).also {
            container.onContentChanged(this)
        }
    }

    override fun setInvStack(slot: Int, stack: ItemStack?) {
        super.setInvStack(slot, stack).also {
            container.onContentChanged(this)
        }
    }
}
