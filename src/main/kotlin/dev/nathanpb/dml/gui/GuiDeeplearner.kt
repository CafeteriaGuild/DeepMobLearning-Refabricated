package dev.nathanpb.dml.gui

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.screen.handler.ContainerDeepLearner
import dev.nathanpb.dml.utils.items
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.text.LiteralText
import net.minecraft.text.StringRenderable
import net.minecraft.text.TranslatableText

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

class GuiDeeplearner (
    container: ContainerDeepLearner
) : HandledScreen<ContainerDeepLearner>(
    container,
    container.playerInventory,
    TranslatableText("item.deepmoblearning.deep_learner")
) {

    companion object {
        val BACKGROUND = identifier("textures/gui/deeplearner_base.png")
    }

    private fun firstDataModelIndex() : Int {
        return handler.inventory.items().indexOfFirst {
            it.item is ItemDataModel
        }.let {
            if (it == -1) 0 else it
        }
    }

    private fun lastDataModelIndex() : Int {
        return handler.inventory.items().indexOfLast {
            it.item is ItemDataModel
        }.let {
            if (it == -1) 0 else it
        }
    }

    private fun nextForwardDataModelIndex() : Int {
        return if (currentSlot != lastDataModelIndex()) {
            handler.inventory.items().mapIndexed { index, stack ->
                Pair(stack, index)
            }.indexOfFirst { (stack, index) ->
                stack.item is ItemDataModel && index > currentSlot
            }.let {
                if (it == -1) 0 else it
            }
        } else currentSlot
    }

    private fun nextReverseDataModelIndex() : Int {
        return if (currentSlot != firstDataModelIndex()) {
            handler.inventory.items().mapIndexed { index, stack ->
                Pair(stack, index)
            }.indexOfLast { (stack, index) ->
                stack.item is ItemDataModel && index < currentSlot
            }.let {
                if (it == -1) 0 else it
            }
        } else currentSlot
    }

    var currentSlot = firstDataModelIndex()
    var tickCount = 0

    private val currentRenderEntity: EntityType<*>?
        get() {
            handler.inventory.getStack(currentSlot)?.item?.let { item ->
                (item as? ItemDataModel)?.category?.tag?.values()?.let { values ->
                    return values.toTypedArray()[(tickCount / 60)% values.size]
                }
            }
            return null
        }

    override fun init() {
        super.init()
        addButton(PaginatorPrevButtonWidget(x + 133, y + 24, this))
        addButton(PaginatorNextButtonWidget(x + 133 + 18, y + 24, this))
    }

    override fun tick() {
        tickCount++
        super.tick()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        this.drawMouseoverTooltip(matrices, mouseX, mouseY)
    }

    override fun drawBackground(matrices: MatrixStack?, delta: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        MinecraftClient.getInstance()?.textureManager?.bindTexture(BACKGROUND)
        this.drawTexture(
            matrices,
            (this.width - this.backgroundWidth) / 2,
            (this.height - this.backgroundHeight) / 2,
            0, 0,
            backgroundWidth,
            backgroundHeight
        )
        // blit((this.width - this.containerWidth) / 2, (this.height - this.containerHeight) / 2, 0, 0, containerWidth, containerHeight)
        drawBackgroundEntity(currentRenderEntity)
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
        textRenderer.draw(matrices, title, 8F, 6F, 0x40A0D3)
        handler.inventory.getStack(currentSlot)?.let { stack ->
            if (stack.item is ItemDataModel) {
                stack.dataModel.let { data ->
                    currentRenderEntity?.let {
                        textRenderer.draw(matrices, it.name, 8F, 20F, 0x373737)
                    }
                    textRenderer.draw(
                        matrices,
                        TranslatableText("tooltip.deepmoblearning.data_model.tier", data.tier().text),
                        46F, 72F, 0x373737
                    )
                    if (!data.tier().isMaxTier()) {
                        textRenderer.draw(
                            matrices,
                            TranslatableText(
                                "tooltip.deepmoblearning.data_model.data_amount_simple",
                                data.dataAmount,
                                data.tier().nextTierOrCurrent().dataAmount
                            ), 46F, 62F, 0x373737
                        )
                    }
                }
            }
        }
    }

    private fun drawBackgroundEntity(entityType: EntityType<*>?) {
        if (entityType == null) return

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

    private abstract class BaseButtonWidget (x: Int, y: Int, val startX: Int = 0)
        : AbstractPressableButtonWidget(x, y, 16, 16, LiteralText("")) {

        override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
            MinecraftClient.getInstance().textureManager.bindTexture(BACKGROUND)
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
            var j = startX
            if (!isActive()) {
                j += width
            }
            this.drawTexture(matrices, x, y, j, 166, width, height)
            // this.blit(x, y, j, 166, width, height)
        }

        override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
            if (isHovered) {
                renderToolTip(matrices, mouseX, mouseY)
            }
            super.render(matrices, mouseX, mouseY, delta)
        }

        abstract fun isActive() : Boolean
    }

    private class PaginatorPrevButtonWidget (x: Int, y: Int, val gui: GuiDeeplearner) : BaseButtonWidget (x, y) {

        override fun isActive() = gui.currentSlot > gui.firstDataModelIndex()

        override fun onPress() {
            if (isActive())
                gui.currentSlot = gui.nextReverseDataModelIndex()
        }

        override fun renderToolTip(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
            gui.renderTooltip(
                matrices,
                TranslatableText("gui.deepmoblearning.previous").formatted(),
                mouseX, mouseY
            )
        }
    }

    private class PaginatorNextButtonWidget (x: Int, y: Int, val gui: GuiDeeplearner) : BaseButtonWidget (x, y, 32) {
        override fun isActive() = gui.currentSlot < gui.lastDataModelIndex()

        override fun onPress() {
            if (isActive())
                gui.currentSlot = gui.nextForwardDataModelIndex()
        }

        override fun renderToolTip(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
            gui.renderTooltip(
                matrices,
                StringRenderable.plain(TranslatableText("gui.deepmoblearning.next").formatted().string),
                mouseX, mouseY
            )
        }
    }
}
