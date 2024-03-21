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

package dev.nathanpb.dml.modular_armor

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.blockEntity.ClientSyncedBlockEntity
import dev.nathanpb.dml.item.ItemPristineMatter
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import dev.nathanpb.dml.modular_armor.inventory.MatterCondenserInventory
import dev.nathanpb.dml.utils.*
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess
import team.reborn.energy.api.base.SimpleEnergyStorage

class BlockEntityMatterCondenser(pos: BlockPos, state: BlockState) :
    ClientSyncedBlockEntity(BLOCK_ENTITY_TYPE, pos, state),
    InventoryProvider
{

    val energyCapacity = modularArmorConfig.machines.matterCondenser.energyCapacity
    val energyIO = modularArmorConfig.machines.matterCondenser.energyIO
    var inventory = MatterCondenserInventory()
    val energyStorage: SimpleEnergyStorage = object : SimpleEnergyStorage(energyCapacity, energyIO, energyIO) {

        override fun onFinalCommit() {
            markDirty()
        }
    }

    companion object {
        val ticker = BlockEntityTicker<BlockEntityMatterCondenser> { world, pos, _, blockEntity ->
            val armorStack = blockEntity.inventory.armorStack
            val inputStack = blockEntity.inventory.pristineInputStack

            if(armorStack.item is ItemModularGlitchArmor) {
                val data = ModularArmorData(armorStack)

                // TODO Remove in 1.21
                if(data.dataAmount > 0) {
                    data.pristineEnergy += (data.dataAmount * 1024).coerceAtMost(64 * 1024)
                    data.dataAmount = -1
                }
                moveToStackPristine(blockEntity.energyStorage, blockEntity.inventory, 0)
            }

            // Insert
            if(inputStack.item is ItemPristineMatter) {
                val pristineEnergyValue = (inputStack.item as ItemPristineMatter).entityCategory.energyValue * modularArmorConfig.machines.matterCondenser.normalToPristineEnergyMultiplier
                if(blockEntity.energyStorage.addEnergy(pristineEnergyValue)) {
                    inputStack.decrement(1)
                    blockEntity.markDirty()
                    blockEntity.sync()
                }
            }

            pushPristineEnergyToAllSides(world, pos, blockEntity.energyStorage)
            moveToStoragePristine(blockEntity.energyStorage, blockEntity.inventory, 1)
            moveToStackPristine(blockEntity.energyStorage, blockEntity.inventory, 2)
            blockEntity.sync()
        }

        val BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            BlockMatterCondenser.IDENTIFIER,
            BlockEntityType.Builder.create(::BlockEntityMatterCondenser, BlockMatterCondenser.BLOCK_MATTER_CONDENSER).build(null)
        ).also {
            SIDED_PRISTINE.registerForBlockEntity(
                { blockEntity, _ -> blockEntity.energyStorage },
                it
            )
        }
    }

    override fun getInventory(state: BlockState?, world: WorldAccess?, pos: BlockPos?) = inventory

    override fun toTag(nbt: NbtCompound) {
        Inventories.writeNbt(nbt, inventory.items())
        nbt.putLong("${MOD_ID}:energy", energyStorage.amount)
    }

    override fun fromTag(nbt: NbtCompound) {
        val list = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
        Inventories.readNbt(nbt, list)
        inventory.setStacks(list)

        energyStorage.amount = nbt.getLong("$MOD_ID:energy")
    }

    override fun toClientTag(nbt: NbtCompound) {
        toTag(nbt)
    }

    override fun fromClientTag(nbt: NbtCompound) {
        fromTag(nbt)
    }

}