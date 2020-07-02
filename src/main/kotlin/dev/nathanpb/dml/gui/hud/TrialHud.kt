/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.gui.hud

import dev.nathanpb.dml.data.TrialPlayerData
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper

class TrialHud : DrawableHelper(), HudRenderCallback {

    companion object {
        val INSTANCE = TrialHud()
    }

    val textRenderer: TextRenderer by lazy {
        MinecraftClient.getInstance().textRenderer
    }

    var data: TrialPlayerData? = null

    override fun onHudRender(p0: Float) {
        data?.let { data ->
            drawString(
                textRenderer,
                "${data.participants} Player${if (data.participants != 1) "s" else ""} Trial",
                4, 4,
                0xFFFFFF
            )

            drawString(
                textRenderer,
                "Wave ${data.currentWave + 1}/${data.maxWaves}",
                4, 12,
                0xFFFFFF
            )
        }
    }


}
