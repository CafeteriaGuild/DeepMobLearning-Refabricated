package dev.nathanpb.dml.gui.screen.handler.widget

/*
class WEntityShowcase {
    fun display() {
        // I have no idea about what mostly of this code do, I just copy/pasted from IngGameHud
        (entityType.create(MinecraftClient.getInstance().world) as? LivingEntity)?.let { entity ->
            RenderSystem.pushMatrix()
            RenderSystem.translatef(x.toFloat() + 24, y.toFloat() + 77, 1050.0F)
            RenderSystem.scalef(1.0f, 1.0f, -1.0f)

            val matrixStack = MatrixStack()
            matrixStack.translate(0.0, 0.0, 1000.0)
            matrixStack.scale(24F, 24F, 24F)

            val quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180F)
            val quaternion2 = Vector3f.POSITIVE_Y.getDegreesQuaternion((tickCount % 360F) * 2F + 150F)

            quaternion.hamiltonProduct(quaternion2)
            matrixStack.multiply(quaternion)
            quaternion2.conjugate()

            val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher
            entityRenderDispatcher.rotation = quaternion2
            entityRenderDispatcher.setRenderShadows(false)

            val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
            entityRenderDispatcher.render(
                entity,
                0.0,
                0.0,
                0.0,
                0.0f,
                0F,
                matrixStack,
                immediate,
                15728880
            )
            immediate.draw()
            entityRenderDispatcher.setRenderShadows(true)
            RenderSystem.popMatrix()
        }
    }
}
*/
