package dev.nathanpb.dml.gui

import dev.nathanpb.dml.container.ContainerDeepLearner
import net.minecraft.client.gui.screen.ingame.ContainerScreen
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
) : ContainerScreen<ContainerDeepLearner>(
    container,
    container.playerInventory,
    TranslatableText("deep_learner")
) {
    override fun drawBackground(delta: Float, mouseX: Int, mouseY: Int) {

    }
}
