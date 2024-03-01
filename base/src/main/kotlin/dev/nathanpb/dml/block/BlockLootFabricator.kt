/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.block

import dev.nathanpb.dml.blockEntity.BlockEntityLootFabricator
import dev.nathanpb.dml.screen.handler.LootFabricatorScreenHandler
import dev.nathanpb.dml.screen.handler.LootFabricatorScreenHandlerFactory
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.screen.ScreenHandler
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class BlockLootFabricator : HorizontalFacingBlock (
    FabricBlockSettings.create()
        .strength(5F, 6F)
), InventoryProvider, BlockEntityProvider {

    init {
        defaultState = stateManager.defaultState
            .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        return if(!world.isClient) BlockEntityLootFabricator.ticker as BlockEntityTicker<T> else null
    }

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        if (world?.isClient == false && pos != null) {
            player?.openHandledScreen(LootFabricatorScreenHandlerFactory(pos) { syncId, inventory, context ->
                LootFabricatorScreenHandler(syncId, inventory, context)
            })
        }
        return ActionResult.SUCCESS
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (state.block !== newState.block) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is BlockEntityLootFabricator) {
                ItemScatterer.spawn(world, pos, blockEntity.inventory)
                ItemScatterer.spawn(world, pos, blockEntity.bufferedInternalInventory)

                world.updateComparators(pos, this)
            }
            super.onStateReplaced(state, world, pos, newState, moved)
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        var blockState = defaultState

        for(direction in ctx.placementDirections) {
            if(direction.axis.isHorizontal) {
                blockState = blockState.with(FACING, direction.opposite)
                if(blockState.canPlaceAt(ctx.world, ctx.blockPos)) return blockState
            }
        }
        return blockState.with(FACING, Direction.NORTH)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = BlockEntityLootFabricator(pos, state)

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityLootFabricator).inventory
    }

    override fun hasComparatorOutput(state: BlockState?) = true

    override fun getComparatorOutput(state: BlockState?, world: World?, pos: BlockPos?): Int {
        return ScreenHandler.calculateComparatorOutput(world?.getBlockEntity(pos))
    }
}