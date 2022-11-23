package dev.nathanpb.dml.simulacrum.block.chamber

import dev.nathanpb.dml.simulacrum.SIMULATION_CHAMBER_ENTITY
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("DEPRECATION")
class BlockSimulationChamber : BlockWithEntity(FabricBlockSettings.of(Material.STONE).hardness(4F).resistance(3000F)) {


    override fun onUse(state: BlockState, world: World, pos: BlockPos?, player: PlayerEntity, hand: Hand?, hit: BlockHitResult?): ActionResult {
        if(world.isClient()) return ActionResult.FAIL
        val screenHandlerFactory = state.createScreenHandlerFactory(world, pos)
        screenHandlerFactory?.let {
            player.openHandledScreen(screenHandlerFactory)
        }
        return ActionResult.SUCCESS
    }

    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity {
        return BlockEntitySimulationChamber(pos, state)
    }

    override fun createScreenHandlerFactory(state: BlockState?, world: World, pos: BlockPos?): NamedScreenHandlerFactory? {
        val blockEntity = world.getBlockEntity(pos)
        return if(blockEntity is NamedScreenHandlerFactory) blockEntity else null
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState? {
        return state.with(facing, rotation.rotate(state.get(facing)))
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState? {
        return state.rotate(mirror.getRotation(state.get(facing)))
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return defaultState.with(facing, ctx.playerFacing.opposite)
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos?, newState: BlockState, moved: Boolean) {
        if(state.block !== newState.block) {
            val blockEntity = world.getBlockEntity(pos)
            if(blockEntity is BlockEntitySimulationChamber) {
                ItemScatterer.spawn(world, pos, blockEntity as BlockEntitySimulationChamber?)
                world.updateComparators(pos, this)
            }
            super.onStateReplaced(state, world, pos, newState, moved)
        }
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun <T : BlockEntity?> getTicker(world: World?, state: BlockState?, type: BlockEntityType<T>?): BlockEntityTicker<T>? {
        return checkType(type, SIMULATION_CHAMBER_ENTITY) { _, pos: BlockPos?, _, blockEntity: BlockEntitySimulationChamber? ->
            if (world != null && blockEntity != null) {
                BlockEntitySimulationChamber.tick(world, pos, state, blockEntity)
            }
        }
    }

    private var facing: DirectionProperty = Properties.HORIZONTAL_FACING
}