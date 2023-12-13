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

import dev.nathanpb.dml.identifier
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.inventory.Inventory
import net.minecraft.util.math.Direction
import team.reborn.energy.api.EnergyStorage
import team.reborn.energy.api.EnergyStorageUtil
import team.reborn.energy.api.base.SimpleEnergyStorage


val SIDED_PRISTINE = BlockApiLookup.get(identifier("sided_pristine"), EnergyStorage::class.java, Direction::class.java)
val ITEM_PRISTINE = ItemApiLookup.get(identifier("item_pristine"), EnergyStorage::class.java, ContainerItemContext::class.java)


fun moveToStorage(energyStorage: EnergyStorage, inventory: Inventory, index: Int) {
    moveToStorage(energyStorage, inventory, index, EnergyStorage.ITEM)
}

fun moveToStack(energyStorage: EnergyStorage, inventory: Inventory, index: Int) {
    moveToStack(energyStorage, inventory, index, EnergyStorage.ITEM)
}

fun moveToStoragePristine(energyStorage: EnergyStorage, inventory: Inventory, index: Int) {
    moveToStorage(energyStorage, inventory, index, ITEM_PRISTINE)
}

fun moveToStackPristine(energyStorage: EnergyStorage, inventory: Inventory, index: Int) {
    moveToStack(energyStorage, inventory, index, ITEM_PRISTINE)
}



private fun moveToStorage(energyStorage: EnergyStorage, inventory: Inventory, index: Int, energyLookup: ItemApiLookup<EnergyStorage, ContainerItemContext>) {
    val stack = inventory.getStack(index)
    if(stack.isEmpty) return

    val stackEnergy = energyLookup.find(stack, ContainerItemContext.ofSingleSlot(InventoryStorage.of(inventory, null).getSlot(index)))
    if(stackEnergy?.supportsExtraction() == true) {
        EnergyStorageUtil.move(stackEnergy, energyStorage, Long.MAX_VALUE, null)
    }
}

private fun moveToStack(energyStorage: EnergyStorage, inventory: Inventory, index: Int, energyLookup: ItemApiLookup<EnergyStorage, ContainerItemContext>) {
    val stack = inventory.getStack(index)
    if(stack.isEmpty) return

    val stackEnergy = energyLookup.find(stack, ContainerItemContext.ofSingleSlot(InventoryStorage.of(inventory, null).getSlot(index)))
    if(stackEnergy?.supportsInsertion() == true) {
        EnergyStorageUtil.move(energyStorage, stackEnergy, Long.MAX_VALUE, null)
    }
}

fun SimpleEnergyStorage.addEnergy(amount: Long): Boolean {
    var commit: Boolean
    Transaction.openOuter().use { transaction ->
        commit = insert(amount, transaction) > 0
        if(commit) {
            transaction.commit()
        }
    }
    return commit
}

fun SimpleEnergyStorage.removeEnergy(amount: Long): Boolean {
    var commit: Boolean
    Transaction.openOuter().use { transaction ->
        commit = extract(amount, transaction) > 0
        if(commit) {
            transaction.commit()
        }
    }
    return commit
}