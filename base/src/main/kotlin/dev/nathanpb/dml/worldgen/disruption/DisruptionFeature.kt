package dev.nathanpb.dml.worldgen.disruption

import com.mojang.serialization.Codec
import dev.nathanpb.dml.block.BLOCK_DISRUPTIONS_CORE
import dev.nathanpb.dml.block.BLOCK_FADING_GLITCHED_TILE
import dev.nathanpb.dml.block.BlockDisruptionsCore
import dev.nathanpb.dml.blockEntity.BlockEntityFadingGlitchedTile
import dev.nathanpb.dml.identifier
import net.minecraft.block.Block
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockPos.Mutable
import net.minecraft.util.math.Direction
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext


class DisruptionFeature(
    codec: Codec<DisruptionFeatureConfig>
): Feature<DisruptionFeatureConfig>(codec) {


    override fun generate(context: FeatureContext<DisruptionFeatureConfig>): Boolean {
        val config = context.config
        val random = context.random
        val origin = context.origin.up()
        val world = context.world


        var hasGeneratedAny = false
        val radius = config.radius.get(random)

        val cx: Int = origin.x
        val cy: Int = origin.y - 1
        val cz: Int = origin.z

        for(blockPos in BlockPos.iterate(
			BlockPos(cx - radius, cy, cz - radius),
            BlockPos(cx + radius, cy, cz + radius)
		)) {
            if(blockPos.getSquaredDistance(cx.toDouble(), blockPos.y.toDouble(), cz.toDouble()) <= (radius * radius + 1).toDouble()) {
                var pos = Mutable(blockPos.x, cy, blockPos.z)
                var offset: Direction? = null
                var canGenerate = true

                if(world.getBlockEntity(pos) != null) { // Abort if block entity
                    canGenerate = false
                } else if(world.getBlockState(pos).isReplaceable) { // Offset downward
                    if(testPos(pos, Direction.DOWN, world)) {
                        offset = Direction.DOWN
                    } else if(!testPos(pos, Direction.UP, world)) { // Abort if block would be floating
                        canGenerate = false
                    }
                } else if(!world.getBlockState(pos.up()).isReplaceable) { // Offset upward
                    if(testPos(pos, Direction.UP, world)) {
                        offset = Direction.UP
                    } else {
                        canGenerate = false
                    }
                }

                if(canGenerate) {
                    if(pos != origin) { // block below core can never be offset
                        pos = verticalOffset(pos, offset)
                    }

                    val blockState = world.getBlockState(pos)
                    world.setBlockState(pos, BLOCK_FADING_GLITCHED_TILE.defaultState, Block.NOTIFY_LISTENERS)
                    (world.getBlockEntity(pos) as? BlockEntityFadingGlitchedTile)?.blockState = blockState
                    hasGeneratedAny = true
                }
            }
        }

        if(hasGeneratedAny) {
            world.setBlockState(origin, BLOCK_DISRUPTIONS_CORE.defaultState.with(BlockDisruptionsCore.FADING, true), Block.NOTIFY_LISTENERS)
            LootableContainerBlockEntity.setLootTable(world, random, origin, identifier("chests/disruption"))
        }

        return hasGeneratedAny
    }

    private fun testPos(pos: BlockPos, direction: Direction, world: StructureWorldAccess): Boolean {
        val offsetPos = pos.offset(direction)
        val isReplaceable = world.getBlockState(offsetPos).isReplaceable
        val isAboveReplaceable = world.getBlockState(offsetPos.up()).isReplaceable

        return !isReplaceable && isAboveReplaceable
    }

    private fun verticalOffset(pos: Mutable, direction: Direction?): Mutable {
        return when(direction) {
            Direction.DOWN -> pos.move(Direction.DOWN)
            Direction.UP -> pos.move(Direction.UP)
            else -> pos
        }
    }

}