/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.gui.hud

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.utils.lerp
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.roundToInt

class FlightBurnoutHud {

    companion object {
        val INSTANCE = FlightBurnoutHud()
        private val TEXTURE = identifier("textures/gui/flight_burnout.png")
        private val TEXTURE_FIRE = identifier("textures/gui/flight_burnout_fire.png")
    }

    private val client = MinecraftClient.getInstance()

    var burnoutTicks = 0
    var maxBurnoutTicks = 0
    var canFly = true

    fun render(matrices: MatrixStack) {
        val player = client.player ?: return
        if (maxBurnoutTicks <= 0 || (canFly && !player.abilities.flying)) {
            return
        }

        val centerX = client.window.scaledWidth / 2
        val centerY = client.window.scaledHeight / 2

        val levelAmount = ((maxBurnoutTicks - burnoutTicks) / maxBurnoutTicks.toDouble()).lerp(0.0, 10.0).roundToInt()
        if (levelAmount > 0) {
            matrices.push()
            client.textureManager.bindTexture(if(canFly) TEXTURE else TEXTURE_FIRE)
            (0 until levelAmount).forEach { index ->
                DrawableHelper.drawTexture(
                    matrices,
                    centerX + 8 + index * 8,
                    centerY + 72,
                    0F, 0F,
                    8, 8,
                    8, 8
                )
            }
            matrices.pop()
        }

    }
}
