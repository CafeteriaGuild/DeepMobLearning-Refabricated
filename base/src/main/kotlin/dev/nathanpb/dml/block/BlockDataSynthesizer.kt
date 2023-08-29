package dev.nathanpb.dml.block

import dev.nathanpb.dml.blockEntity.BlockEntityDataSynthesizer
import dev.nathanpb.dml.screen.handler.DataSynthesizerScreenHandler
import dev.nathanpb.dml.screen.handler.DataSynthesizerScreenHandlerFactory
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


class BlockDataSynthesizer  : HorizontalFacingBlock(
    FabricBlockSettings.create()
        .hardness(4F)
        .resistance(3000F)
), InventoryProvider, BlockEntityProvider {

    init {
        defaultState = stateManager.defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }


    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        return if (world.isClient) null else BlockEntityDataSynthesizer.ticker as BlockEntityTicker<T>
    }

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        if (world?.isClient == false && pos != null) {
            player?.openHandledScreen(DataSynthesizerScreenHandlerFactory(pos) { syncId, inventory, context ->
                DataSynthesizerScreenHandler(syncId, inventory, context)
            })
        }
        return ActionResult.SUCCESS
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if(state.block !== newState.block) {
            val blockEntity = world.getBlockEntity(pos)
            if(blockEntity is BlockEntityDataSynthesizer) {
                ItemScatterer.spawn(world, pos, blockEntity.inventory)
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

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = BlockEntityDataSynthesizer(pos, state)

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory {
        return (world.getBlockEntity(pos) as BlockEntityDataSynthesizer).inventory
    }

    override fun hasComparatorOutput(state: BlockState?) = true

    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos))
    }
}