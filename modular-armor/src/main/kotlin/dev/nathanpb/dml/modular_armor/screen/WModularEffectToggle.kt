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

package dev.nathanpb.dml.modular_armor.screen

import dev.nathanpb.dml.modular_armor.core.ModularEffect
import dev.nathanpb.dml.screen.handler.widget.WDarkToggleButton
import dev.nathanpb.dml.utils.RenderUtils
import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.properties.Delegates

class WModularEffectToggle : WDarkToggleButton() {

    var effect by Delegates.observable<ModularEffect<*>?>(null) { _, _, value ->
        setLabel(
            value?.name?.setStyle(RenderUtils.STYLE) ?: Text.empty()
        )
    }

    override fun addTooltip(tooltip: TooltipBuilder) {
        if(effect == null) return
        tooltip.add(effect!!.name.setStyle(RenderUtils.STYLE).formatted(Formatting.UNDERLINE))
        tooltip.add(effect!!.getEffectInfo().text())
        tooltip.add(Text.empty())
        tooltip.add(effect!!.description.formatted(Formatting.GRAY))
    }


    override fun paint(ctx: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        ScreenDrawing.drawString(
            ctx,
            label!!.asOrderedText(),
            x, y + 6,
            -0x1
        )

        val toggleSize = 18
        val toggleOffset = x + parent!!.width - (toggleSize + 14)
        ScreenDrawing.texturedRect(
            ctx,
            toggleOffset, y,
            toggleSize, toggleSize,
            if(isOn) onImage else offImage,
            -0x1
        )
    }

}