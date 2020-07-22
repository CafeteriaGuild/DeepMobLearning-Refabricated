/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.data.EntityCategory
import dev.nathanpb.dml.entity.FakePlayerEntity
import dev.nathanpb.dml.inventory.LootFabricatorInventory
import dev.nathanpb.dml.item.ItemPristineMatter
import dev.nathanpb.dml.utils.items
import dev.nathanpb.dml.utils.setStacks
import dev.nathanpb.dml.utils.simulateLootDroppedStacks
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Tickable
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess
import kotlin.math.min

class BlockEntityLootFabricator
    : BlockEntity(BLOCKENTITY_LOOT_FABRICATOR),
    InventoryProvider,
    PropertyDelegateHolder,
    Tickable
{

    companion object {
        const val PROCESS_TIME = 20 * 10
        const val MATTER_ENTITY_EXCHANGE = 16
    }

    private val _propertyDelegate = ArrayPropertyDelegate(2)
    private var isDumpingBufferedInventory = false
    val bufferedInternalInventory = SimpleInventory(64)
    val inventory = LootFabricatorInventory().apply {
        addListener {
            dumpInternalInventory()
        }
    }

    private var progress = 0

    init {
        propertyDelegate[1] = PROCESS_TIME
    }

    override fun tick() {
        (world as? ServerWorld)?.let { world ->
            if (bufferedInternalInventory.isEmpty) {
                val stack = inventory.stackInInputSlot ?: return resetProgress()
                val item = stack.item as? ItemPristineMatter ?: return resetProgress()

                if (progress >= PROCESS_TIME) {
                    val generatedLoot = generateLoot(world, item.category).toTypedArray()
                    bufferedInternalInventory.setStacks(
                        DefaultedList.copyOf(ItemStack.EMPTY, *generatedLoot)
                    )
                    stack.decrement(1)
                    dumpInternalInventory()
                } else {
                    progress++
                    propertyDelegate[0] = progress
                    return
                }

                resetProgress()
            }
        }
    }

    private fun generateLoot(world: ServerWorld, category: EntityCategory): List<ItemStack> {
        return (0 until MATTER_ENTITY_EXCHANGE).map {
            category.tag
                .getRandom(java.util.Random())
                .simulateLootDroppedStacks(world, FakePlayerEntity(world), DamageSource.GENERIC)
        }.flatten().let { stacks ->
            SimpleInventory(stacks.size).also { tempInventory ->
                stacks.forEach { tempInventory.addStack(it) }
            }
        }.items()
    }

    // if you know a better way to do this please tell me
    private fun dumpInternalInventory() {
        if (bufferedInternalInventory.isEmpty || isDumpingBufferedInventory) return
        isDumpingBufferedInventory = true

        fun canCombine(a: ItemStack, b: ItemStack) = a.item === b.item && ItemStack.areTagsEqual(a, b)

        fun transfer(source: ItemStack, target: ItemStack) {
            val i: Int = min(inventory.maxCountPerStack, target.maxCount)
            val j: Int = min(source.count, i - target.count)
            if (j > 0) {
                target.increment(j)
                source.decrement(j)
                markDirty()
            }
        }

        LootFabricatorInventory.OUTPUT_SLOTS.forEach { invIndex ->
            bufferedInternalInventory
                .items()
                .filterNot(ItemStack::isEmpty)
                .forEach { buffStack ->
                    val invStack = inventory.getStack(invIndex)
                    if (invStack.isEmpty) {
                        inventory.setStack(invIndex, buffStack.copy())
                        buffStack.count = 0
                    } else if (canCombine(invStack, buffStack)) {
                        transfer(buffStack, invStack)
                        inventory.markDirty()
                    }
                }
        }
        isDumpingBufferedInventory = false
    }

    private fun resetProgress() {
        if (progress != 0) {
            progress = 0
            propertyDelegate[0] = 0
        }
    }

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityLootFabricator).inventory
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        return super.toTag(tag).also {
            if (tag != null) {
                CompoundTag().let { invTag ->
                    Inventories.toTag(invTag, inventory.items())
                    tag.put("deepmoblearning:inventory", invTag)
                }

                CompoundTag().let { invTag ->
                    Inventories.toTag(invTag, bufferedInternalInventory.items())
                    tag.put("deepmoblearning:bufferedInventory", invTag)
                }

                tag.putInt("deepmoblearning:progress", progress)
            }
        }
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag).also {
            if (tag != null) {
                val stacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
                Inventories.fromTag(tag.getCompound("deepmoblearning:inventory"), stacks)
                inventory.setStacks(stacks)

                val stacksBufferedInventory = DefaultedList.ofSize(bufferedInternalInventory.size(), ItemStack.EMPTY)
                Inventories.fromTag(tag.getCompound("deepmoblearning:bufferedInventory"), stacksBufferedInventory)
                bufferedInternalInventory.setStacks(stacksBufferedInventory)

                progress = tag.getInt("deepmoblearning:progress")
            }
        }
    }

    override fun getPropertyDelegate() = _propertyDelegate
}
