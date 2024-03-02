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
import dev.nathanpb.dml.baseConfig
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.inventory.LootFabricatorInventory
import dev.nathanpb.dml.item.ItemPristineMatter
import dev.nathanpb.dml.recipe.RECIPE_LOOT_FABRICATOR
import dev.nathanpb.dml.utils.*
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.fabricmc.fabric.api.entity.FakePlayer
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
import kotlin.properties.Delegates

class BlockEntityLootFabricator(pos: BlockPos, state: BlockState) :
    BlockEntity(BLOCKENTITY_LOOT_FABRICATOR, pos, state),
    InventoryProvider,
    PropertyDelegateHolder,
    RecipeInputProvider
{

    /**
     * 0 - [progress]
     * 1 - [maxProgress] (const)
     * 2 - [SimpleEnergyStorage.amount]
     * 3 - [energyCapacity] (const)
     */
    private val _propertyDelegate = ArrayPropertyDelegate(4)
    private var isDumpingBufferedInventory = false
    val bufferedInternalInventory = SimpleInventory(64)
    val inventory = LootFabricatorInventory().apply {
        addListener {
            dumpInternalInventory()
        }
    }

    private var progress by Delegates.observable(0) { _, _, newValue ->
        propertyDelegate[0] = newValue
    }
    private val maxProgress = baseConfig.machines.lootFabricator.processTime
    private val energyCapacity = baseConfig.machines.lootFabricator.energyCapacity
    private val energyInput = baseConfig.machines.lootFabricator.energyInput
    val energyStorage: SimpleEnergyStorage = object : SimpleEnergyStorage(energyCapacity, energyInput, 0) {

        override fun onFinalCommit() {
            markDirty()
            propertyDelegate[2] = amount.toInt()
        }
    }

    init {
        propertyDelegate[1] = maxProgress
        propertyDelegate[3] = energyCapacity.toInt()
    }
    
    companion object {
        private fun calculateEnergyCost(pristineMatter: ItemStack): Long {
            if(!baseConfig.machines.lootFabricator.isEnergyCostScaledToMatterType) {
                return baseConfig.machines.lootFabricator.fixedCost
            }
            if(pristineMatter.item !is ItemPristineMatter) throw IllegalStateException("Loot Fabricator has non-pristine matter item at its input!")
            return ((pristineMatter.item as ItemPristineMatter).entityCategory.energyValue.toFloat() * baseConfig.machines.lootFabricator.energyCostMultiplier).toLong()
        }

        val ticker = BlockEntityTicker<BlockEntityLootFabricator> { world, _, _, blockEntity ->

            moveToStorage(blockEntity.energyStorage, blockEntity.inventory, LootFabricatorInventory.ENERGY_INPUT)

            if(blockEntity.bufferedInternalInventory.isEmpty) {
                val pristineMatterStack = blockEntity.inventory.stackInInputSlot
                    ?: return@BlockEntityTicker blockEntity.resetProgress()

                val recipe = world.recipeManager.getFirstMatch(RECIPE_LOOT_FABRICATOR, blockEntity.inventory, world).orElse(null)
                    ?: return@BlockEntityTicker blockEntity.resetProgress()

                val energyCost = calculateEnergyCost(pristineMatterStack)

                if(blockEntity.energyStorage.amount >= energyCost) {
                    if(blockEntity.progress >= blockEntity.maxProgress) {
                        val generatedLoot = blockEntity.generateLoot((world as ServerWorld), recipe.category).also {
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
                        pristineMatterStack.decrement(1)
                        blockEntity.dumpInternalInventory()

                        blockEntity.energyStorage.amount -= energyCost
                        blockEntity.propertyDelegate[2] = blockEntity.energyStorage.amount.toInt()
                        blockEntity.markDirty()
                    } else {
                        blockEntity.progress++
                        blockEntity.markDirty()
                        return@BlockEntityTicker
                    }
                } else {
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
        if(progress == 0) return
        progress = 0
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
                energyStorage.amount = tag.getLong("${MOD_ID}:energy").also {
                    propertyDelegate[2] = it.toInt()
                }
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
