package dev.nathanpb.dml.utils

import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import kotlin.math.min

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */


fun ItemStack.toTag() = this.toTag(CompoundTag())

fun combineStacksIfPossible(source: ItemStack, target: ItemStack, maxInventoryCountPerStack: Int): Boolean {
    fun canCombine(source: ItemStack, target: ItemStack) : Boolean {
        return (
            source.item === target.item
            && target.count < target.maxCount
            && ItemStack.areTagsEqual(source, target)
        )
    }

    fun transfer(source: ItemStack, target: ItemStack) {
        val i: Int = min(maxInventoryCountPerStack, target.maxCount)
        val j: Int = min(source.count, i - target.count)
        if (j > 0) {
            target.increment(j)
            source.decrement(j)
        }
    }

    return canCombine(source, target).also {
        if (it) {
            transfer(source, target)
        }
    }
}
