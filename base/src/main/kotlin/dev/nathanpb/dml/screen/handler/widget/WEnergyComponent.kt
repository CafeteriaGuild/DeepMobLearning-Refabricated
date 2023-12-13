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

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.utils.RenderUtils
import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WSprite
import net.minecraft.inventory.Inventory
import net.minecraft.text.Text


class WEnergyComponent(
    private val energyIndex: Int,
    private val maxEnergyIndex: Int,
    val inventory: Inventory,
    inputIndex: Int,
    outputIndex: Int,
    val isPristineEnergy: Boolean = false // don't use outside modular-armor!
): WPlainPanel() {


    private val energyBar = object : WBar(
        RenderUtils.ENERGY_BAR_BACKGROUND,
        if(isPristineEnergy) RenderUtils.PRISTINE_ENERGY_BAR else RenderUtils.ENERGY_BAR,
        energyIndex,
        maxEnergyIndex,
        Direction.UP
    ) {

        override fun tick() {
            updateEnergyText(getHost()?.propertyDelegate?.get(energyIndex))
        }

    }
    private val outputSlot = WItemSlot.of(
        inventory,
        outputIndex,
        1,
        1
    )
    private val inputSlot = WItemSlot.of(
        inventory,
        inputIndex,
        1,
        1
    )

    private val arrow = WSprite(identifier("textures/gui/energy_bar_arrow.png"))
    private val arrow2 = WSprite(identifier("textures/gui/energy_bar_arrow.png"))


    init {
        add(outputSlot, 0, 0)
        add(arrow, 0, 18, 18, 6)
        add(energyBar, 0, 24, 1*18, 30)
        add(arrow2, 0, 54, 18, 6)
        add(inputSlot, 0, 3*18+6)
    }



    fun updateEnergyText(energy: Int?) {
        energyBar.apply {

            val translationKey = if(isPristineEnergy) {
                "text.dml-refabricated.pristine_energy.short"
            } else {
                "text.dml-refabricated.energy.short"
            }

            withTooltip(
                Text.translatable(
                    translationKey,
                    RenderUtils.formatAccordingToLanguage().format(energy)
                ).apply {
                    style = if(isPristineEnergy) RenderUtils.STYLE else RenderUtils.ENERGY_STYLE
                }
            )
        }
    }

}