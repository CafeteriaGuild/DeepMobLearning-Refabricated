/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.modular_armor.inventory

import dev.nathanpb.dml.item.ItemPristineMatter
import dev.nathanpb.dml.modular_armor.ItemModularGlitchArmor
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction

class MatterCondenserInventory : SimpleInventory(3), SidedInventory {
    companion object {
        const val ARMOR_SLOT = 0
        const val PRISTINE_ENERGY_IN = 1
        const val PRISTINE_ENERGY_OUT = 2
    }

    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = slot == ARMOR_SLOT

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return when(stack.item) {
            is ItemPristineMatter -> slot == PRISTINE_ENERGY_IN
            is ItemModularGlitchArmor -> slot == ARMOR_SLOT
            else -> true
        }
    }

    override fun getAvailableSlots(side: Direction?): IntArray {
      return intArrayOf(ARMOR_SLOT, PRISTINE_ENERGY_IN, PRISTINE_ENERGY_OUT)
    }

    var armorStack: ItemStack
        get() = getStack(ARMOR_SLOT)
        set(value) = setStack(ARMOR_SLOT, value)

    var pristineInputStack: ItemStack
        get() = getStack(PRISTINE_ENERGY_IN)
        set(value) = setStack(PRISTINE_ENERGY_IN, value)

    var pristineOutputStack: ItemStack
        get() = getStack(PRISTINE_ENERGY_OUT)
        set(value) = setStack(PRISTINE_ENERGY_OUT, value)

}
