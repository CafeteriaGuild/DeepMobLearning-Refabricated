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

import dev.nathanpb.dml.item.ItemModularGlitchArmor
import dev.nathanpb.dml.item.ItemPristineMatter
import dev.nathanpb.dml.utils.toIntArray
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction

class MatterCondenserInventory : SimpleInventory(7), SidedInventory {
    companion object {
        const val ARMOR_SLOT = 0
        val MATTER_SLOTS = (1..6).toIntArray()
    }

    override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?) = slot == ARMOR_SLOT

    override fun canInsert(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return when (stack?.item) {
            is ItemPristineMatter -> slot in MATTER_SLOTS
            is ItemModularGlitchArmor -> slot == ARMOR_SLOT
            else -> false
        }
    }

    override fun getAvailableSlots(side: Direction?) = MATTER_SLOTS + ARMOR_SLOT

    var stackInArmorSlot: ItemStack
        get() = getStack(ARMOR_SLOT)
        set(value) = setStack(ARMOR_SLOT, value)

    var matterStacks: DefaultedList<ItemStack>
        get() = DefaultedList.copyOf(ItemStack.EMPTY, *MATTER_SLOTS.map(this::getStack).toTypedArray())
        set(value) = value.take(MATTER_SLOTS.size).forEachIndexed { index, stack -> setStack(index + 1, stack) }
}
