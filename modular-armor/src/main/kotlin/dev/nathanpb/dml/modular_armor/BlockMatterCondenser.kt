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

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.itemgroup.ITEM_GROUP_KEY
import dev.nathanpb.dml.modular_armor.ItemModularGlitchArmor.Companion.GLITCH_BOOTS
import dev.nathanpb.dml.modular_armor.screen.MatterCondenserScreenHandler
import dev.nathanpb.dml.modular_armor.screen.MatterCondenserScreenHandlerFactory
import dev.nathanpb.dml.utils.getFullCapacityStack
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.screen.ScreenHandler
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.Rarity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class BlockMatterCondenser : HorizontalFacingBlock(
    FabricBlockSettings.create()
        .strength(5F, 6F)
), InventoryProvider, BlockEntityProvider {

    companion object {
        val BLOCK_MATTER_CONDENSER = BlockMatterCondenser()
        val IDENTIFIER = identifier("matter_condenser")

        fun register() {
            Registry.register(Registries.BLOCK, IDENTIFIER, BLOCK_MATTER_CONDENSER)
            Registry.register(Registries.ITEM, IDENTIFIER, BlockItem(BLOCK_MATTER_CONDENSER, FabricItemSettings().rarity(Rarity.RARE)))

            ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register {
                it.addAfter(getFullCapacityStack(GLITCH_BOOTS), BLOCK_MATTER_CONDENSER)
            }
        }
    }

    init {
        defaultState = stateManager.defaultState
            .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
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

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity?, hand: Hand, hit: BlockHitResult): ActionResult {
        if (!world.isClient) {
            player?.openHandledScreen(MatterCondenserScreenHandlerFactory(pos) {
                syncId, playerInventory, context -> MatterCondenserScreenHandler(syncId, playerInventory, context)
            })
        }
        return ActionResult.SUCCESS
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if (state.block !== newState.block) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is BlockEntityMatterCondenser) {
                ItemScatterer.spawn(world, pos, (blockEntity as BlockEntityMatterCondenser?)!!.inventory)

                world.updateComparators(pos, this)
            }
            super.onStateReplaced(state, world, pos, newState, moved)
        }
    }

    override fun hasComparatorOutput(state: BlockState?) = true

    override fun getComparatorOutput(state: BlockState?, world: World, pos: BlockPos?): Int {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos))
    }

    override fun <T : BlockEntity?> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if(!world.isClient) BlockEntityMatterCondenser.ticker as BlockEntityTicker<T> else null
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BlockEntityMatterCondenser(pos, state)
    }

    override fun getInventory(state: BlockState?, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityMatterCondenser).inventory
    }

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return voxelShape
    }

    private val voxelShape: VoxelShape = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.99, 16.0)

}