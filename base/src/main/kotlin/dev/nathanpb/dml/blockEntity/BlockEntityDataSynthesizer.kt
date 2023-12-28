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

package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.inventory.DataSynthesizerInventory
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.utils.*
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess
import team.reborn.energy.api.base.SimpleEnergyStorage


class BlockEntityDataSynthesizer(pos: BlockPos, state: BlockState) :
    ClientSyncedBlockEntity(BLOCKENTITY_DATA_SYNTHESIZER, pos, state),
    InventoryProvider,
    PropertyDelegateHolder
{

    /**
     * 0 - [SimpleEnergyStorage.amount]
     * 1 - [energyCapacity] (const)
     * 2 - [progress]
     * 3 - [maxProgress] (const)
     * 4 - [canProgress]
     */
    private val _propertyDelegate = ArrayPropertyDelegate(5)
    val inventory = DataSynthesizerInventory()
    private val energyCapacity = 196608L // TODO Add as config value
    val energyStorage: SimpleEnergyStorage = object : SimpleEnergyStorage(energyCapacity, 8192, 8192) {

        override fun onFinalCommit() {
            markDirty()
            propertyDelegate[0] = amount.toInt()
        }
    }
    var progress: Int = 0
        set(value) {
            field = value
            propertyDelegate[2] = value
        }

    val maxProgress = 3 * 20
    var canProgress = false

    init {
        propertyDelegate[1] = energyCapacity.toInt()
        propertyDelegate[3] = maxProgress
    }

    companion object {
        private val dataEnergyValue = 3072 // TODO use simulation cost * 8-ish

        val ticker = BlockEntityTicker<BlockEntityDataSynthesizer> { world, pos, _, blockEntity ->

            val dataModelStack = blockEntity.inventory.getStack(0)
            if(!dataModelStack.isEmpty && dataModelStack.item is ItemDataModel) {
                if(blockEntity.energyStorage.amount <= (blockEntity.energyCapacity - dataEnergyValue)) {
                    if(dataModelStack.hasSimUnrestrictedData()) {
                        blockEntity.canProgress = true
                        blockEntity.propertyDelegate[4] = 1
                        blockEntity.markDirty()
                        blockEntity.sync()
                    } else { // ensure the laser on the UI doesn't flicker by only removing flag on the start of the next loop, if invalid
                        removeCanProgressFlag(blockEntity)
                    }
                    if(blockEntity.canProgress) {
                        blockEntity.progress++
                        blockEntity.propertyDelegate[2] = blockEntity.progress
                        blockEntity.sync()

                        if(blockEntity.progress >= blockEntity.maxProgress) {
                            dataModelStack.dataModel.dataAmount--
                            blockEntity.energyStorage.amount += dataEnergyValue
                            blockEntity.propertyDelegate[0] = blockEntity.energyStorage.amount.toInt()
                            resetProgress(blockEntity)
                            blockEntity.markDirty()
                            blockEntity.sync()
                        }
                    }
                } else {
                    removeCanProgressFlag(blockEntity)
                }

                // Remove invalid 'simulated' tag
                if(dataModelStack.dataModel.simulated && dataModelStack.dataModel.dataAmount <= 0) {
                    dataModelStack.dataModel.simulated = false
                    blockEntity.markDirty()
                }
            } else {
                removeCanProgressFlag(blockEntity)
            }

            pushEnergyToAllSides(world, pos, blockEntity.energyStorage)
            moveToStorage(blockEntity.energyStorage, blockEntity.inventory, 1)
            moveToStack(blockEntity.energyStorage, blockEntity.inventory, 2)
        }

        private fun removeCanProgressFlag(blockEntity: BlockEntityDataSynthesizer) {
            blockEntity.canProgress = false
            blockEntity.propertyDelegate[4] = 0
            resetProgress(blockEntity)
            blockEntity.markDirty()
            blockEntity.sync()
        }

        private fun resetProgress(blockEntity: BlockEntityDataSynthesizer) {
            blockEntity.progress = 0
            blockEntity.propertyDelegate[2] = 0
        }
    }

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityDataSynthesizer).inventory
    }


    override fun toTag(nbt: NbtCompound) {
        NbtCompound().let { invTag ->
            Inventories.writeNbt(invTag, inventory.items())
            nbt.put("${MOD_ID}:inventory", invTag)
        }

        nbt.putLong("${MOD_ID}:energy", energyStorage.amount)
        nbt.putBoolean("${MOD_ID}:can_progress", canProgress)
        nbt.putInt("${MOD_ID}:progress", progress)
    }


    override fun fromTag(nbt: NbtCompound) {
        val stacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
        Inventories.readNbt(nbt.getCompound("${MOD_ID}:inventory"), stacks)
        inventory.setStacks(stacks)

        energyStorage.amount = nbt.getLong("${MOD_ID}:energy").also {
            propertyDelegate[0] = it.toInt()
        }
        canProgress = nbt.getBoolean("${MOD_ID}:can_progress")
        progress = nbt.getInt("${MOD_ID}:progress")
    }

    override fun toClientTag(nbt: NbtCompound) {
        NbtCompound().let { invTag ->
            Inventories.writeNbt(invTag, inventory.items())
            nbt.put("${MOD_ID}:inventory", invTag)
        }

        canProgress = nbt.getBoolean("${MOD_ID}:can_progress")
        nbt.putInt("${MOD_ID}:progress", progress)
    }

    override fun fromClientTag(nbt: NbtCompound) {
        val stacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
        Inventories.readNbt(nbt.getCompound("${MOD_ID}:inventory"), stacks)
        inventory.setStacks(stacks)

        nbt.putBoolean("${MOD_ID}:can_progress", canProgress)
        progress = nbt.getInt("${MOD_ID}:progress")
    }

    override fun getPropertyDelegate() = _propertyDelegate

}