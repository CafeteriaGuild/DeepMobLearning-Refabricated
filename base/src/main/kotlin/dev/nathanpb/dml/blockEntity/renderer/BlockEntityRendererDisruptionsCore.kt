package dev.nathanpb.dml.blockEntity.renderer

import dev.nathanpb.dml.blockEntity.BlockEntityDisruptionsCore
import dev.nathanpb.dml.identifier
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.model.ShulkerEntityModel
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

class BlockEntityRendererDisruptionsCore(
    ctx: BlockEntityRendererFactory.Context
): BlockEntityRenderer<BlockEntityDisruptionsCore> {

    private val model: ShulkerEntityModel<*>

    init {
        model = ShulkerEntityModel<ShulkerEntity>(ctx.getLayerModelPart(EntityModelLayers.SHULKER))
    }


    override fun render(
        blockEntity: BlockEntityDisruptionsCore,
        f: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider?,
        i: Int,
        j: Int
    ) {
        val spriteIdentifier = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, identifier("block/disruptions_core"))

        matrixStack.push()
        matrixStack.translate(0.5F, 0.5F, 0.5F)
        matrixStack.scale(0.9995F, 0.9995F, 0.9995F)
        matrixStack.scale(0.9995F, 0.9995F, 0.9995F)
        matrixStack.multiply(Direction.UP.rotationQuaternion)
        matrixStack.scale(1F, -1F, -1F)
        matrixStack.translate(0F, -1F, 0F)

        val lid = model.lid
        lid.setPivot(0F, 24F - blockEntity.getAnimationProgress(f) * 0.5F * 16F, 0F)
        lid.yaw = 225F * blockEntity.getAnimationProgress(f) * 0.017453292F

        val vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider) {
            texture: Identifier -> RenderLayer.getEntityCutoutNoCull(texture)
        }
        model.render(matrixStack, vertexConsumer, i, j, 1F, 1F, 1F, 1F)
        matrixStack.pop()
    }

}