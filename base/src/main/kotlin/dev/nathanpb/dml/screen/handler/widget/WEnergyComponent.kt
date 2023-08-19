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

package dev.nathanpb.dml.screen.handler.widget

import dev.nathanpb.dml.utils.RenderUtils
import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.gui.widgets.Widgets.withTooltip
import net.minecraft.inventory.Inventory
import net.minecraft.text.Text

class WEnergyComponent( // TODO add PE energy support (along style changes)
    private val energyIndex: Int,
    private val maxEnergyIndex: Int,
    val inventory: Inventory,
    batterySlotIndex: Int,
    energyBarHeight: Int = 3*18
): WPlainPanel() {

    private val energyBar = object : WBar(RenderUtils.PROGRESS_BAR_BACKGROUND, RenderUtils.PROGRESS_BAR, energyIndex, maxEnergyIndex, WBar.Direction.UP) {

        override fun tick() {
            updateEnergyText(getHost()?.propertyDelegate?.get(energyIndex))
        }

    }
    private val batterySlot = WItemSlot.of(inventory, batterySlotIndex, 1, 1)

    init {
        add(energyBar, 0, 0, 1*18, energyBarHeight)
        add(batterySlot, 0, energyBarHeight+4)
    }



    fun updateEnergyText(energy: Int?) {
        energyBar.apply {
            withTooltip(Text.literal("$energy E").apply {// FIXME use translation here
                style = RenderUtils.STYLE
            })
        }
    }

}