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

package dev.nathanpb.dml.utils

import dev.nathanpb.dml.baseConfig
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.item.ItemDataModel
import net.minecraft.item.ItemStack
import kotlin.math.min

fun combineStacksIfPossible(source: ItemStack, target: ItemStack, maxInventoryCountPerStack: Int): Boolean {
    fun canCombine(source: ItemStack, target: ItemStack) : Boolean {
        return (
            source.item === target.item
            && target.count < target.maxCount
            && ItemStack.areEqual(source, target)
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
        it.`if` {
            transfer(source, target)
        }
    }
}

fun ItemStack.hasSimUnrestrictedData(): Boolean {
    if(this.item !is ItemDataModel) return false
    return this.dataModel.dataAmount > 0 && ((baseConfig.dataModel.hasSimulatedDataRestrictions && !this.dataModel.simulated) || !baseConfig.dataModel.hasSimulatedDataRestrictions)
}
