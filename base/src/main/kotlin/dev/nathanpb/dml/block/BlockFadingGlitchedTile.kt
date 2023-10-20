package dev.nathanpb.dml.block

import dev.nathanpb.dml.blockEntity.BlockEntityFadingGlitchedTile
import dev.nathanpb.dml.utils.RenderUtils.Companion.ALT_GLITCH_PARTICLE
import dev.nathanpb.dml.utils.RenderUtils.Companion.GLITCH_PARTICLE
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.random.Random
import net.minecraft.world.World

class BlockFadingGlitchedTile: BlockWithEntity(
    FabricBlockSettings.copy(Blocks.STONE).mapColor(MapColor.CYAN).dropsNothing()
) {

    private fun transformBlock(world: World, pos: BlockPos) {
        val blockState = (world.getBlockEntity(pos) as? BlockEntityFadingGlitchedTile)?.blockState
        world.setBlockState(pos, blockState)

        if(world.getRandom().nextInt(10) > 6) {
            world.playSound(
                null,
                pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.BLOCKS,
                1F, 1F
            )
        }
    }


    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        transformBlock(world, pos)
        return ActionResult.success(world.isClient())
    }


    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
        transformBlock(world, pos)

        BlockPos.stream(
            Box(
                pos.up().north().west(), // Top Corner
                pos.down().south().east() // Bottom Corner
            )
        )
        .filter { world.getBlockState(it).isOf(this) }
        .forEach {
            world.scheduleBlockTick(it, BLOCK_FADING_GLITCHED_TILE, 6)
        }
    }


    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        if(random.nextInt(100) > 0) return // 0.01%

        val x = pos.x.toDouble() + 0.1 + random.nextDouble() * 0.8
        val y = pos.y.toDouble() + 1.2
        val z = pos.z.toDouble() + 0.1 + random.nextDouble() * 0.8

        var particle = GLITCH_PARTICLE
        if(random.nextInt(10) > 7) {
            particle = ALT_GLITCH_PARTICLE
        }

        world.addParticle(particle, x, y, z, 0.0, 0.0, 0.0)
    }


    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BlockEntityFadingGlitchedTile(pos, state)
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

}