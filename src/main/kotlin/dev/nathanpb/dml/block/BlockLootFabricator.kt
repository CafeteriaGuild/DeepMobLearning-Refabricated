/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.block

import dev.nathanpb.dml.blockEntity.BlockEntityLootFabricator
import dev.nathanpb.dml.screen.handler.LootFabricatorHandler
import dev.nathanpb.dml.screen.handler.LootFabricatorScreenHandlerFactory
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.ServerWorldAccess
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class BlockLootFabricator : HorizontalFacingBlock (
    FabricBlockSettings.of(Material.STONE)
        .hardness(4F)
        .resistance(3000F)
), InventoryProvider, BlockEntityProvider {

    init {
        defaultState = stateManager.defaultState
            .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        if (world?.isClient == false && pos != null) {
            player?.openHandledScreen(LootFabricatorScreenHandlerFactory(pos) { syncId, inventory, context ->
                LootFabricatorHandler(syncId, inventory, context)
            })
        }
        return ActionResult.SUCCESS
    }

    override fun afterBreak(world: World?, player: PlayerEntity?, pos: BlockPos?, state: BlockState?, blockEntity: BlockEntity?, stack: ItemStack?) {
        if (world is ServerWorldAccess && blockEntity is BlockEntityLootFabricator) {
            ItemScatterer.spawn(world, pos, blockEntity.inventory)
            ItemScatterer.spawn(world, pos, blockEntity.bufferedInternalInventory)
        }
        super.afterBreak(world, player, pos, state, blockEntity, stack)
    }


    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return defaultState.with(FACING, ctx?.playerFacing?.opposite)
    }

    override fun createBlockEntity(world: BlockView?) = BlockEntityLootFabricator()

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityLootFabricator).inventory
    }

    override fun hasComparatorOutput(state: BlockState?) = true

    override fun getComparatorOutput(state: BlockState?, world: World?, pos: BlockPos?): Int {
        return ScreenHandler.calculateComparatorOutput(world?.getBlockEntity(pos))
    }
}
