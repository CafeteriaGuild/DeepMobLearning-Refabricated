package dev.nathanpb.dml.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import dev.nathanpb.dml.container.ContainerDeepLearner
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ItemDataModel
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.text.TranslatableText
import kotlin.random.Random

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

class GuiDeeplearner (
    container: ContainerDeepLearner
) : ContainerScreen<ContainerDeepLearner>(
    container,
    container.playerInventory,
    TranslatableText("item.deepmoblearning.deep_learner")
) {
    var currentSlot: Int = 0
    var tickCount = 0;

    override fun tick() {
        tickCount++
        super.tick()
    }

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground()
        super.render(mouseX, mouseY, delta)
        this.drawMouseoverTooltip(mouseX, mouseY)
    }

    override fun drawBackground(delta: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        minecraft?.textureManager?.bindTexture(identifier("textures/gui/deeplearner_base.png"))
        blit((this.width - this.containerWidth) / 2, (this.height - this.containerHeight) / 2, 0, 0, containerWidth, containerHeight)
        container.inventory.getInvStack(currentSlot)?.let { stack ->
            if (stack.item is ItemDataModel) {
                stack.dataModel.entity?.let {
                    drawBackgroundEntity(it)
                }
            }
        }
    }

    override fun drawForeground(mouseX: Int, mouseY: Int) {
        super.font.draw(title.asFormattedString(), 8F, 6F, 0x40A0D3)
    }

    private fun drawBackgroundEntity(entityType: EntityType<*>) {
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

            val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderManager
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
