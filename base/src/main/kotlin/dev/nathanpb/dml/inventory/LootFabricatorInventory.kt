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

package dev.nathanpb.dml.inventory

import dev.nathanpb.dml.utils.toIntArray
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction

class LootFabricatorInventory : SimpleInventory(11), SidedInventory {

    companion object {
        const val INPUT_SLOT = 0
        val OUTPUT_SLOTS = (1..9).toIntArray()
        const val ENERGY_INPUT = 10
    }

    val stackInInputSlot: ItemStack?
        get() = getStack(INPUT_SLOT)

    // TODO find a way to check with the world's recipeManager
    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) = slot == INPUT_SLOT
    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = slot in OUTPUT_SLOTS
    override fun getAvailableSlots(side: Direction?) = (0 until size()).toIntArray()
}
