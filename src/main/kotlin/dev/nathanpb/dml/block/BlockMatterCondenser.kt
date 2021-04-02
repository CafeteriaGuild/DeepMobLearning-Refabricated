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

package dev.nathanpb.dml.block

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.blockEntity.BlockEntityMatterCondenser
import dev.nathanpb.dml.screen.handler.MatterCondenserHandler
import dev.nathanpb.dml.screen.handler.MatterCondenserScreenHandlerFactory
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
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

class BlockMatterCondenser : HorizontalFacingBlock(
    FabricBlockSettings.of(Material.STONE)
        .hardness(4F)
        .resistance(3000F)
), InventoryProvider, BlockEntityProvider {
    init {
        defaultState = stateManager.defaultState
            .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return defaultState.with(FACING, ctx?.playerFacing?.opposite)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity?, hand: Hand, hit: BlockHitResult): ActionResult {
        if (!world.isClient) {
            player?.openHandledScreen(MatterCondenserScreenHandlerFactory(pos) {
                syncId, playerInventory, context -> MatterCondenserHandler(syncId, playerInventory, context)
            })
        }
        return ActionResult.SUCCESS
    }

    override fun afterBreak(world: World?, player: PlayerEntity?, pos: BlockPos?, state: BlockState?, blockEntity: BlockEntity?, stack: ItemStack?) {
        if (world is ServerWorldAccess && blockEntity is BlockEntityMatterCondenser) {
            ItemScatterer.spawn(world, pos, blockEntity.inventory)
        }
        super.afterBreak(world, player, pos, state, blockEntity, stack)
    }

    override fun createBlockEntity(world: BlockView?): BlockEntity? {
        return BlockEntityMatterCondenser()
    }

    override fun getInventory(state: BlockState?, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityMatterCondenser).inventory
    }
}
