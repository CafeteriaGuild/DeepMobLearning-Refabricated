package dev.nathanpb.dml.block

import dev.nathanpb.dml.blockEntity.BLOCKENTITY_DISRUPTIONS_CORE
import dev.nathanpb.dml.blockEntity.BlockEntityDisruptionsCore
import dev.nathanpb.dml.screen.handler.DisruptionsCoreScreenHandler
import dev.nathanpb.dml.screen.handler.DisruptionsCoreScreenHandlerFactory
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class BlockDisruptionsCore: BlockWithEntity(FabricBlockSettings.copy(Blocks.SHULKER_BOX).luminance { 7 }) {

    private val shape = createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0)


    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand?,
        hit: BlockHitResult
    ): ActionResult {
        if(world.isClient) return ActionResult.SUCCESS
        if(player.isSpectator) return ActionResult.CONSUME

        val blockEntity = world.getBlockEntity(pos)
        if(blockEntity is BlockEntityDisruptionsCore) {
            if(canOpen(state, world, pos, blockEntity)) {
                player.openHandledScreen(DisruptionsCoreScreenHandlerFactory(pos) { syncId, inventory, context ->
                    DisruptionsCoreScreenHandler(syncId, inventory, context)
                })

                val belowPos = pos.down()
                if(world.getBlockState(belowPos).isOf(BLOCK_FADING_GLITCHED_TILE)) {
                    world.scheduleBlockTick(pos, BLOCK_DISRUPTIONS_CORE, 4*20)
                    world.scheduleBlockTick(belowPos, BLOCK_FADING_GLITCHED_TILE, 4*20 + 6)
                }
            }
            return ActionResult.CONSUME
        }
        return ActionResult.PASS
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        world.setBlockState(pos, Blocks.AIR.defaultState, Block.NOTIFY_ALL)
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(state))
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BlockEntityDisruptionsCore(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return checkType(
            type, BLOCKENTITY_DISRUPTIONS_CORE
        ) { world: World, pos: BlockPos, state: BlockState, blockEntity: BlockEntityDisruptionsCore ->
            BlockEntityDisruptionsCore.tick(
                world,
                pos,
                state,
                blockEntity
            )
        }
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.ENTITYBLOCK_ANIMATED
    }

    private fun canOpen(state: BlockState, world: World, pos: BlockPos, entity: BlockEntityDisruptionsCore): Boolean {
        if(entity.animationStage != BlockEntityDisruptionsCore.AnimationStage.CLOSED) return true
        val box = ShulkerEntity.calculateBoundingBox(Direction.UP, 0.0f, 0.5f).offset(pos).contract(1.0E-6)
        return world.isSpaceEmpty(box)
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        val blockEntity: BlockEntity? = world.getBlockEntity(pos)
        if(itemStack.hasCustomName() && blockEntity is BlockEntityDisruptionsCore) {
           blockEntity.customName = itemStack.name
        }
    }

    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        newState: BlockState,
        moved: Boolean
    ) {
        if(state.isOf(newState.block)) return

        val blockEntity = world.getBlockEntity(pos)
        if(blockEntity is BlockEntityDisruptionsCore) world.updateComparators(pos, state.block)

        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun getSidesShape(state: BlockState, world: BlockView, pos: BlockPos?): VoxelShape? {
        val blockEntity = world.getBlockEntity(pos)
        return if(blockEntity is BlockEntityDisruptionsCore && !blockEntity.also {
            }.suffocates()) {
            shape
        } else VoxelShapes.fullCube()
    }

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        val blockEntity = world.getBlockEntity(pos)
        return if(blockEntity is BlockEntityDisruptionsCore) {
            VoxelShapes.cuboid(blockEntity.getBoundingBox())
        } else VoxelShapes.fullCube()
    }

    override fun hasComparatorOutput(state: BlockState?): Boolean {
        return true
    }

    override fun getComparatorOutput(state: BlockState?, world: World, pos: BlockPos?): Int {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos) as Inventory?)
    }

}

