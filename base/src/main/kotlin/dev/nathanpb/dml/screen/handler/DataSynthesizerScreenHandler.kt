/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.screen.handler

import com.mojang.blaze3d.systems.RenderSystem
import dev.nathanpb.dml.blockEntity.BlockEntityDataSynthesizer
import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.screen.handler.widget.WEnergyComponent
import dev.nathanpb.dml.screen.handler.widget.WInfoBubbleWidget
import dev.nathanpb.dml.screen.handler.widget.WInfoBubbleWidget.Companion.WARNING_BUBBLE
import dev.nathanpb.dml.screen.handler.widget.WMuteButton
import dev.nathanpb.dml.utils.RenderUtils
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.networking.NetworkSide
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WSprite
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.data.Texture
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.client.render.GameRenderer.getPositionTexProgram
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper


class DataSynthesizerScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    val ctx: ScreenHandlerContext
) : SyncedGuiDescription(
    HANDLER_DATA_SYNTHESIZER,
    syncId, playerInventory,
    getBlockInventory(ctx),
    getBlockPropertyDelegate(ctx)
) {
    init {
        val MUTE_TOGGLE_ID = identifier("data_synthesizer_mute_toggle")
        val blockPos = ctx.get { _, u -> { u }}.get().invoke()

        ScreenNetworking.of(
            this,
            NetworkSide.SERVER
        ).receive(MUTE_TOGGLE_ID) { _ ->
            val blockEntity = world.getBlockEntity(blockPos)
            if(world.getBlockEntity(blockPos) is BlockEntityDataSynthesizer) {
                (blockEntity as BlockEntityDataSynthesizer).mute = !blockEntity.mute
                blockEntity.markDirty()
                blockEntity.sync()
            }
        }

        val root = WPlainPanel()
        setRootPanel(root)
        root.insets = Insets.ROOT_PANEL

        val simulatedDataBubble = WInfoBubbleWidget(
            WARNING_BUBBLE,
            listOf(
                Text.translatable("text.dml-refabricated.simulated_data.warning").formatted(Formatting.DARK_RED)
            ),
            true
        )
        root.add(simulatedDataBubble, (5 * 18) + 1, (2 * 18) + 6, 8, 8)

        val dataModelSlot = WItemSlot.of(blockInventory, 0, 1, 1).apply {
            setInputFilter { stack ->
                stack.item is ItemDataModel
            }

            icon = TextureIcon(identifier("textures/gui/slot_background/data_model_slot_background.png"))
            @Suppress("RedundantIf")
            addChangeListener { _, _, _, stack ->
                if(!stack.isEmpty && (config.dataModel.hasSimulatedDataRestrictions && stack.dataModel.simulated)) {
                    simulatedDataBubble.hidden = false
                } else {
                    simulatedDataBubble.hidden = true
                }
            }
        }
        root.add(dataModelSlot, 4 * 18, (2 * 18) + 6)

        val progressThingy = WProgressLine((2 * 18) + 7)
        root.add(progressThingy, (4 * 18) + 1, progressThingy.initialY, 16, 1)

        val energyComponent = WEnergyComponent(0, 1, blockInventory, 1, 2)
        root.add(energyComponent, 0, (1 * 18) - 6)

        val soundButton = object : WMuteButton(propertyDelegate[5]) {
            override fun toggleMute() {
                ScreenNetworking.of(
                    this@DataSynthesizerScreenHandler,
                    NetworkSide.CLIENT
                ).send(MUTE_TOGGLE_ID) { _ -> }

            }
        }
        root.add(soundButton, (8 * 18) - 2, (1 * 18) - 6, 20, 20)

        root.add(createPlayerInventoryPanel(), 0, (5 * 18) + 2)
        root.validate(this)

        (blockInventory as? SimpleInventory)?.addListener {
            sendContentUpdates()
        }


    }

    override fun canUse(entity: PlayerEntity?) = true

    override fun addPainters() {
        rootPanel.backgroundPainter = RenderUtils.DEFAULT_BACKGROUND_PAINTER
    }

    override fun getTitleColor(): Int {
        return RenderUtils.TITLE_COLOR
    }

    inner class WProgressLine(
        var initialY: Int
    ): WSprite(
        Texture(identifier("textures/block/palette.png"), 0.0625F, 0F, 0.125F, 0.0625F)
    ) {

        private var hidden = true
        private val areaSize = 15F

        init {
            initialY += 7
        }

        override fun tick() {
            if(host!!.propertyDelegate!!.get(4) == 0) { // canProgress
                hidden = true
                return
            }
            hidden = false


            val percentage = MathHelper.clamp(
                host!!.propertyDelegate!!.get(2).toFloat() / host!!.propertyDelegate!!.get(3).toFloat(),
                0F,
                1F
            )

            y = if(percentage <= 0.5F) { // First half, downward
                initialY + Math.round(areaSize * percentage * 2F)
            } else { // Second half, upward
                initialY + Math.round(areaSize * 2F * (1F - percentage))
            }

        }

        override fun paint(context: DrawContext?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
            if(hidden) return

            val texture = frames[0]
            val tessellator = Tessellator.getInstance()
            val buffer = tessellator.buffer
            val model = context!!.matrices.peek().positionMatrix
            RenderSystem.enableBlend()
            RenderSystem.setShaderTexture(0, texture.image)
            RenderSystem.setShaderColor(1F, 1F, 1F, 0.8F)
            RenderSystem.setShader(::getPositionTexProgram)
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
            buffer.vertex(model, x.toFloat(), (y + height).toFloat(), 1000F).texture(texture.u1, texture.v2).next()
            buffer.vertex(model, (x + width).toFloat(), (y + height).toFloat(), 1000F).texture(texture.u2, texture.v2).next()
            buffer.vertex(model, (x + width).toFloat(), y.toFloat(), 1000F).texture(texture.u2, texture.v1).next()
            buffer.vertex(model, x.toFloat(), y.toFloat(), 1000F).texture(texture.u1, texture.v1).next()
            BufferRenderer.drawWithGlobalProgram(buffer.end())
            RenderSystem.disableBlend()
        }
    }

}