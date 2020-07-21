/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.inventory.LootFabricatorInventory
import dev.nathanpb.dml.utils.items
import dev.nathanpb.dml.utils.setStacks
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

class BlockEntityLootFabricator : BlockEntity(BLOCKENTITY_LOOT_FABRICATOR), InventoryProvider {

    val inventory = LootFabricatorInventory()

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityLootFabricator).inventory
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        return super.toTag(tag).also {
            if (tag != null) {
                Inventories.toTag(tag, inventory.items())
            }
        }
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag).also {
            if (tag != null) {
                val stacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
                Inventories.fromTag(tag, stacks)
                inventory.setStacks(stacks)
            }
        }
    }
}
