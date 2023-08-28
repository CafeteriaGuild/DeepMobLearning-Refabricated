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
import dev.nathanpb.dml.utils.items
import dev.nathanpb.dml.utils.setStacks
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
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
    BlockEntity(BLOCKENTITY_DATA_SYNTHESIZER, pos, state),
    InventoryProvider,
    PropertyDelegateHolder
{

    private val _propertyDelegate = ArrayPropertyDelegate(2)
    val inventory = DataSynthesizerInventory()

    val energyStorage: SimpleEnergyStorage = object : SimpleEnergyStorage(propertyDelegate[1].toLong(), 0, 8192) {

        override fun onFinalCommit() {
            markDirty()
            propertyDelegate[0] = amount.toInt()
        }

    }

    init {
        propertyDelegate[1] = 196608 // TODO Add as config value
    }

    companion object {
        private val dataEnergyValue = 3072 // TODO Add as config value

        val ticker = BlockEntityTicker<BlockEntityDataSynthesizer> { _, _, _, blockEntity ->

            val dataModelStack = blockEntity.inventory.getStack(0)
            if(blockEntity.energyStorage.amount <= (blockEntity.propertyDelegate[1] - dataEnergyValue) && !dataModelStack.isEmpty) {
                if(dataModelStack.item is ItemDataModel && dataModelStack.dataModel.dataAmount > 0) {
                    dataModelStack.dataModel.dataAmount--
                    blockEntity.energyStorage.amount += dataEnergyValue
                    blockEntity.propertyDelegate[0] = blockEntity.energyStorage.amount.toInt()
                    blockEntity.markDirty()
                }
            }

            // TODO add energy slot support
        }
    }


    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityDataSynthesizer).inventory
    }

    override fun writeNbt(tag: NbtCompound?) {
        return super.writeNbt(tag).also {
            if (tag != null) {
                tag.putLong("${MOD_ID}:energy", energyStorage.amount)

                NbtCompound().let { invTag ->
                    Inventories.writeNbt(invTag, inventory.items())
                    tag.put("${MOD_ID}:inventory", invTag)
                }
            }
        }
    }

    override fun readNbt(tag: NbtCompound?) {
        super.readNbt(tag).also {
            if (tag != null) {
                energyStorage.amount = tag.getLong("${MOD_ID}:energy").also {
                    propertyDelegate[0] = it.toInt()
                }

                val stacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
                Inventories.readNbt(tag.getCompound("${MOD_ID}:inventory"), stacks)
                inventory.setStacks(stacks)
            }
        }
    }

    override fun getPropertyDelegate() = _propertyDelegate

}
