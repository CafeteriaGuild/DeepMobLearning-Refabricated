package dev.nathanpb.dml.modular_armor.renderer

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.mixin.BakedModelManagerAccessor
import dev.nathanpb.dml.modular_armor.BlockEntityMatterCondenser
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.random.Random


class BlockEntityRendererMatterCondenser(
    ctx: BlockEntityRendererFactory.Context
): BlockEntityRenderer<BlockEntityMatterCondenser> {

    override fun render(
        blockEntity: BlockEntityMatterCondenser,
        tickDelta: Float,
        ms: MatrixStack,
        vcp: VertexConsumerProvider,
        l: Int,
        overlay: Int
    ) {
        val light = WorldRenderer.getLightmapCoordinates(blockEntity.world, blockEntity.pos)
        val coreModel = (MinecraftClient.getInstance().bakedModelManager as? BakedModelManagerAccessor)?.models?.get(identifier("block/matter_condenser_core")) ?: return

        if(isNewCycle) {
            startTime = blockEntity.world!!.time.toFloat()
            isNewCycle = false
        } else {
            if(blockEntity.world!!.time.toFloat() >= startTime + cycleDuration) {
                isNewCycle = true
            }
        }

        val current = blockEntity.world!!.time.toFloat() - startTime
        val percentage = current * 360F / cycleDuration

        ms.push()
        ms.translate(0.5, 0.4375, 0.5)
        ms.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(percentage))
        val buffer = vcp.getBuffer(RenderLayer.getEntityCutout(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE))
        coreModel.getQuads(null, null, Random.create()).forEach { quad ->
            buffer.quad(ms.peek(), quad, 1F, 1F, 1F, light, overlay)
        }
        ms.pop()
    }

    private var startTime: Float = 0F
    private var isNewCycle = true

    private val cycleDuration = 30F * 20F

}