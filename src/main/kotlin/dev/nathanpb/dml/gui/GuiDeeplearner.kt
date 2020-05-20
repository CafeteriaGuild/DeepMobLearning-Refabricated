package dev.nathanpb.dml.gui

import com.mojang.blaze3d.platform.GlStateManager
import dev.nathanpb.dml.container.ContainerDeepLearner
import dev.nathanpb.dml.identifier
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier

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

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground()
        super.render(mouseX, mouseY, delta)
        this.drawMouseoverTooltip(mouseX, mouseY)
    }

    override fun drawBackground(delta: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        minecraft?.textureManager?.bindTexture(identifier("textures/gui/deeplearner_base.png"))
        blit((this.width - this.containerWidth) / 2, (this.height - this.containerHeight) / 2, 0, 0, containerWidth, containerHeight)
    }

    override fun drawForeground(mouseX: Int, mouseY: Int) {
        super.font.draw(title.asFormattedString(), 8F, 6F, 0x40A0D3)
    }
}
