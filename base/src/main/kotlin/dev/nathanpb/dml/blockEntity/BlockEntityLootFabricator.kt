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
import dev.nathanpb.dml.config
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.inventory.LootFabricatorInventory
import dev.nathanpb.dml.recipe.RECIPE_LOOT_FABRICATOR
import dev.nathanpb.dml.utils.combineStacksIfPossible
import dev.nathanpb.dml.utils.items
import dev.nathanpb.dml.utils.setStacks
import dev.nathanpb.dml.utils.simulateLootDroppedStacks
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.fabricmc.fabric.api.entity.FakePlayer
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.recipe.RecipeInputProvider
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.registry.Registries
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess
import team.reborn.energy.api.base.SimpleEnergyStorage

class BlockEntityLootFabricator(pos: BlockPos, state: BlockState) :
    BlockEntity(BLOCKENTITY_LOOT_FABRICATOR, pos, state),
    InventoryProvider,
    PropertyDelegateHolder,
    RecipeInputProvider
{

    private val _propertyDelegate = ArrayPropertyDelegate(4)
    private var isDumpingBufferedInventory = false
    val bufferedInternalInventory = SimpleInventory(64)
    val inventory = LootFabricatorInventory().apply {
        addListener {
            dumpInternalInventory()
        }
    }

    private var progress = 0
    val energyStorage: SimpleEnergyStorage = object : SimpleEnergyStorage(propertyDelegate[3].toLong(), 8192, 0) {

        init {
            propertyDelegate[2] = amount.toInt()
        }

        override fun onFinalCommit() {
            markDirty()
            propertyDelegate[2] = amount.toInt()
            println("onFinalCommit | $amount")
        }

        override fun insert(maxAmount: Long, transaction: TransactionContext?): Long {
            StoragePreconditions.notNegative(maxAmount)

            val inserted = Math.min(maxInsert, Math.min(maxAmount, capacity - amount))


            if (inserted > 0) {
                println("inserting $inserted")

                updateSnapshots(transaction)
                amount += inserted
                return inserted
            }

            return 0
        }

    }

    init {
        propertyDelegate[1] = config.lootFabricator.processTime
        propertyDelegate[3] = 16384 // TODO Add as config value
    }
    
    companion object {
        val ticker = BlockEntityTicker<BlockEntityLootFabricator> { world, _, _, blockEntity ->
            (world as? ServerWorld)?.let { _ ->
                if (blockEntity.bufferedInternalInventory.isEmpty) {
                    val stack = blockEntity.inventory.stackInInputSlot
                        ?: return@BlockEntityTicker blockEntity.resetProgress()

                    val recipe = world.recipeManager.getFirstMatch(RECIPE_LOOT_FABRICATOR, blockEntity.inventory, world).orElse(null)
                        ?: return@BlockEntityTicker blockEntity.resetProgress()

                    if(blockEntity.energyStorage.amount >= 256) {
                        if (blockEntity.progress >= config.lootFabricator.processTime) {
                            val generatedLoot = blockEntity.generateLoot(world, recipe.category).also {
                                // O(n²) goes brrrr
                                it.forEach { stackSource ->
                                    it.forEach { stackTarget ->
                                        combineStacksIfPossible(
                                            stackSource,
                                            stackTarget,
                                            blockEntity.bufferedInternalInventory.maxCountPerStack
                                        )
                                    }
                                }
                            }.filterNot(ItemStack::isEmpty)
                            blockEntity.bufferedInternalInventory.setStacks(
                                DefaultedList.copyOf(ItemStack.EMPTY, *generatedLoot.toTypedArray())
                            )
                            stack.decrement(1)
                            blockEntity.dumpInternalInventory()

                            blockEntity.energyStorage.amount -= 256
                            blockEntity.propertyDelegate[2] = blockEntity.energyStorage.amount.toInt()
                            blockEntity.markDirty()
                        } else {
                            blockEntity.progress++
                            blockEntity.propertyDelegate[0] = blockEntity.progress
                            blockEntity.markDirty()
                            return@BlockEntityTicker
                        }
                    }

                    blockEntity.resetProgress()
                }
            }
        }
    }
    
    private fun generateLoot(world: ServerWorld, category: EntityCategory): List<ItemStack> {
        val entityList = Registries.ENTITY_TYPE.iterateEntries(category.tagKey).filter{true}
        return (0 until category.exchangeRatio).map {
            entityList.random().value()
                .simulateLootDroppedStacks(world, FakePlayer.get(world), world.damageSources.generic())
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


        LootFabricatorInventory.OUTPUT_SLOTS.forEach { invIndex ->
            bufferedInternalInventory
                .items()
                .filterNot(ItemStack::isEmpty)
                .forEach { buffStack ->
                    val invStack = inventory.getStack(invIndex)
                    if (invStack.isEmpty) {
                        inventory.setStack(invIndex, buffStack.copy())
                        buffStack.count = 0
                    } else {
                        if (combineStacksIfPossible(buffStack, invStack, inventory.maxCountPerStack)) {
                            inventory.markDirty()
                        }
                    }
                }
        }
        isDumpingBufferedInventory = false
    }

    private fun resetProgress() {
        if (progress == 0) return
        progress = 0
        propertyDelegate[0] = 0
        markDirty()
    }

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityLootFabricator).inventory
    }

    override fun writeNbt(tag: NbtCompound?) {
        return super.writeNbt(tag).also {
            if (tag != null) {
                tag.putLong("${MOD_ID}:energy", energyStorage.amount)
                tag.putInt("${MOD_ID}:progress", progress)

                NbtCompound().let { invTag ->
                    Inventories.writeNbt(invTag, inventory.items())
                    tag.put("${MOD_ID}:inventory", invTag)
                }

                NbtCompound().let { invTag ->
                    Inventories.writeNbt(invTag, bufferedInternalInventory.items())
                    tag.put("${MOD_ID}:bufferedInventory", invTag)
                }
            }
        }
    }

    override fun readNbt(tag: NbtCompound?) {
        super.readNbt(tag).also {
            if (tag != null) {
                energyStorage.amount = tag.getLong("${MOD_ID}:energy")
                progress = tag.getInt("${MOD_ID}:progress")

                val stacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY)
                Inventories.readNbt(tag.getCompound("${MOD_ID}:inventory"), stacks)
                inventory.setStacks(stacks)

                val stacksBufferedInventory = DefaultedList.ofSize(bufferedInternalInventory.size(), ItemStack.EMPTY)
                Inventories.readNbt(tag.getCompound("${MOD_ID}:bufferedInventory"), stacksBufferedInventory)
                bufferedInternalInventory.setStacks(stacksBufferedInventory)
            }
        }
    }

    override fun getPropertyDelegate() = _propertyDelegate

    override fun provideRecipeInputs(finder: RecipeMatcher) {
        finder.addInput(inventory.stackInInputSlot)
    }
}
