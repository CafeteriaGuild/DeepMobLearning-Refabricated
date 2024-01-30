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
import dev.nathanpb.dml.utils.RenderUtils.Companion.ENERGY_BAR
import dev.nathanpb.dml.utils.RenderUtils.Companion.ENERGY_BAR_ARROW
import dev.nathanpb.dml.utils.RenderUtils.Companion.ENERGY_BAR_BACKGROUND
import dev.nathanpb.dml.utils.RenderUtils.Companion.ENERGY_BAR_BACKGROUND_BIG
import dev.nathanpb.dml.utils.RenderUtils.Companion.ENERGY_BAR_BIG
import dev.nathanpb.dml.utils.RenderUtils.Companion.ENERGY_STYLE
import dev.nathanpb.dml.utils.RenderUtils.Companion.PRISTINE_ENERGY_BAR
import dev.nathanpb.dml.utils.RenderUtils.Companion.PRISTINE_ENERGY_BAR_BIG
import dev.nathanpb.dml.utils.RenderUtils.Companion.STYLE
import dev.nathanpb.dml.utils.getShortEnergyKey
import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WSprite
import net.minecraft.inventory.Inventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier


class WEnergyComponent(
    private val energyIndex: Int,
    private val maxEnergyIndex: Int,
    inventory: Inventory,
    inputIndex: Int,
    outputIndex: Int?,
    private val isPristineEnergy: Boolean = false /** do NOT use outside modular-armor! */
): WPlainPanel() {

    companion object { // this should by all means be on the inner class, but kotlin doesn't allow it :(
        fun energyBarBackground(big: Boolean): Identifier {
            return if(big) ENERGY_BAR_BACKGROUND_BIG else ENERGY_BAR_BACKGROUND
        }

        fun energyBar(big: Boolean, isPristineEnergy: Boolean): Identifier {
            return if(big) {
                if(isPristineEnergy) PRISTINE_ENERGY_BAR_BIG else ENERGY_BAR_BIG
            } else {
                if(isPristineEnergy) PRISTINE_ENERGY_BAR else ENERGY_BAR
            }
        }
    }

    constructor( // input only
        energyIndex: Int,
        maxEnergyIndex: Int,
        inventory: Inventory,
        inputIndex: Int,
        isPristineEnergy: Boolean = false
    ): this(
        energyIndex,
        maxEnergyIndex,
        inventory,
        inputIndex,
        null,
        isPristineEnergy
    )

    init {
        val inputSlot = WItemSlot.of(inventory, inputIndex)

        if(outputIndex != null) {
            val outputSlot = WItemSlot.of(inventory, outputIndex)

            add(outputSlot, 0, 0)
            add(WSprite(ENERGY_BAR_ARROW), 0, 19, 18, 4)
            add(WEnergyBar(false), 0, 24, 1 * 18, 30)
            add(WSprite(ENERGY_BAR_ARROW), 0, 3 * 18, 18, 4)
            add(inputSlot, 0, (3 * 18) + 4)
        } else {
            add(WEnergyBar(true), 0, 0, 1 * 18, 54)
            add(WSprite(ENERGY_BAR_ARROW), 0, 3 * 18, 18, 4)
            add(inputSlot, 0, (3 * 18) + 4)
        }
    }


    inner class WEnergyBar private constructor(
        backgroundTexture: Identifier,
        barTexture: Identifier
    ) : WBar(
        backgroundTexture,
        barTexture,
        energyIndex,
        maxEnergyIndex,
        Direction.UP
    ) {

        constructor(big: Boolean = false): this(
            energyBarBackground(big),
            energyBar(big, isPristineEnergy)
        )


        override fun tick() {
            this.apply {
                withTooltip(
                    Text.translatable(
                        getShortEnergyKey(isPristineEnergy),
                        RenderUtils.formatAccordingToLanguage().format(getHost()?.propertyDelegate?.get(energyIndex))
                    ).apply {
                        style = if(isPristineEnergy) STYLE else ENERGY_STYLE
                    }
                )
            }
        }

    }

}