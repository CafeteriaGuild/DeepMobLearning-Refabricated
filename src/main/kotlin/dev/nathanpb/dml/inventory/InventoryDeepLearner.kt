package dev.nathanpb.dml.inventory

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

import dev.nathanpb.dml.item.deepLearnerInventory
import dev.nathanpb.dml.screen.handler.ContainerDeepLearner
import net.minecraft.inventory.SimpleInventory

class InventoryDeepLearner(val container: ContainerDeepLearner) :
    SimpleInventory(container.stack.deepLearnerInventory.size) {

    override fun markDirty() {
        super.markDirty()
        container.onContentChanged(this)
    }
}
