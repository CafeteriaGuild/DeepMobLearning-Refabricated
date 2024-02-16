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
import dev.nathanpb.dml.item.ItemPristineMatter
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import dev.nathanpb.dml.modular_armor.inventory.MatterCondenserInventory
import dev.nathanpb.dml.utils.*
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess
import team.reborn.energy.api.base.SimpleEnergyStorage

class BlockEntityMatterCondenser (pos: BlockPos, state: BlockState) :
    BlockEntity(BLOCK_ENTITY_TYPE, pos, state),
    InventoryProvider,
    PropertyDelegateHolder
{

    var inventory = MatterCondenserInventory()
    private val propertyDelegate = ArrayPropertyDelegate(2).also {
        it[1] = 262144
    }

    val energyStorage: SimpleEnergyStorage = object : SimpleEnergyStorage(propertyDelegate[1].toLong(), 8192, 8192) {

        override fun onFinalCommit() {
            markDirty()
            propertyDelegate[0] = amount.toInt()
        }

    }

    companion object {
        private val pristineMatterEnergyValue = 1024L

        val ticker = BlockEntityTicker<BlockEntityMatterCondenser> { _, _, _, blockEntity ->
            val armorStack = blockEntity.inventory.armorStack
            val inputStack = blockEntity.inventory.pristineInputStack
            val outputStack = blockEntity.inventory.pristineOutputStack

            if(armorStack.item is ItemModularGlitchArmor) {
                val data = ModularArmorData(armorStack)

                // TODO Remove in 1.21
                if(data.dataAmount > 0) {
                    data.pristineEnergy += (data.dataAmount * pristineMatterEnergyValue).coerceAtMost(64 * 1024)
                    data.dataAmount = -1
                }
                moveToStackPristine(blockEntity.energyStorage, blockEntity.inventory, 0)
            }

            // Insert
            if(inputStack.item is ItemPristineMatter) { // TODO replace similar checks to check with the tag
                if(blockEntity.energyStorage.addEnergy(pristineMatterEnergyValue)) {
                    blockEntity.propertyDelegate[0] = blockEntity.energyStorage.amount.toInt()
                    inputStack.decrement(1)
                    blockEntity.markDirty()
                }
            }


            // FIXME pushEnergyToAllSides(world, pos, blockEntity.energyStorage)
            moveToStoragePristine(blockEntity.energyStorage, blockEntity.inventory, 1)
            moveToStackPristine(blockEntity.energyStorage, blockEntity.inventory, 2)


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

    override fun getPropertyDelegate() = propertyDelegate

    override fun writeNbt(tag: NbtCompound) {
        Inventories.writeNbt(tag, inventory.items())
        tag.putLong("${MOD_ID}:energy", energyStorage.amount)
    }

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)
        val list = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
        Inventories.readNbt(tag, list)
        inventory.setStacks(list)

        energyStorage.amount = tag.getLong("$MOD_ID:energy").also {
            propertyDelegate[0] = it.toInt()
        }
    }
}
