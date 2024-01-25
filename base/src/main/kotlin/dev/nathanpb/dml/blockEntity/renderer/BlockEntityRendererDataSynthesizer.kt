package dev.nathanpb.dml.blockEntity.renderer

import dev.nathanpb.dml.block.BLOCK_DATA_SYNTHESIZER
import dev.nathanpb.dml.blockEntity.BlockEntityDataSynthesizer
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.mixin.BakedModelManagerAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.state.property.Properties.HORIZONTAL_FACING
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.random.Random


class BlockEntityRendererDataSynthesizer(
    ctx: BlockEntityRendererFactory.Context
): BlockEntityRenderer<BlockEntityDataSynthesizer> {


    override fun render(
        blockEntity: BlockEntityDataSynthesizer,
        tickDelta: Float,
        ms: MatrixStack,
        vcp: VertexConsumerProvider,
        l: Int,
        overlay: Int
    ) {
        if(blockEntity.inventory.dataModelStack.isEmpty) return
        if(!blockEntity.world!!.getBlockState(blockEntity.pos).isOf(BLOCK_DATA_SYNTHESIZER)) return

        val client = MinecraftClient.getInstance()
        val direction: Direction = blockEntity.world!!.getBlockState(blockEntity.pos).get(HORIZONTAL_FACING)
        val light = WorldRenderer.getLightmapCoordinates(blockEntity.world, blockEntity.pos.offset(direction))


        // Data Model
        ms.push()
        ms.translate(0.5, 0.5, 0.5)

        if(direction != Direction.NORTH) { // no rotation required
            val angles = angleMap[direction] as Angle
            ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(angles.x))
            ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angles.y))
            ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angles.z))
        }

        ms.scale(0.5F, 0.5F, 0.5F)
        client.itemRenderer.renderItem(
            blockEntity.inventory.dataModelStack,
            ModelTransformationMode.FIXED,
            light,
            OverlayTexture.DEFAULT_UV,
            ms,
            vcp,
            blockEntity.world,
            blockEntity.pos.asLong().toInt()
        )
        ms.pop()

        // Grid
        val gridModel = (client.bakedModelManager as? BakedModelManagerAccessor)?.models?.get(identifier("block/data_synthesizer_grid")) ?: return
        val percentage = MathHelper.clamp(
            blockEntity.progress.toFloat() / blockEntity.maxProgress.toFloat(),
            0F,
            1F
        )

        val y = if(percentage <= 0.5F) { // First half, downward
            MathHelper.lerp(percentage * 2F, 0.0315F, 0.77F)
        } else { // Second half, upward
            MathHelper.lerp(2F * (1F - percentage), 0.0315F, 0.77F)
        }

        ms.push()
        ms.translate(0F, -y, 0F)
        val buffer = vcp.getBuffer(RenderLayer.getEntityCutout(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)) // FIXME use translucent
        gridModel.getQuads(null, null, Random.create()).forEach { quad ->
            buffer.quad(ms.peek(), quad, 1F, 1F, 1F, light, overlay)
        }
        ms.pop()
    }


    private val angleMap: HashMap<Direction, Angle> = hashMapOf(
        Direction.SOUTH to Angle(180F, 0F, 180F),
        Direction.WEST to Angle(180F, 90F, 180F),
        Direction.EAST to Angle(90F, 270F, 90F)
    )

    data class Angle(val x: Float, val y: Float, val z: Float)

}