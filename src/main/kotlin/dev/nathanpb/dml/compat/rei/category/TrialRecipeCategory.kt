/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.compat.rei.category

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.block.BLOCK_TRIAL_KEYSTONE
import dev.nathanpb.dml.compat.rei.display.TrialRecipeDisplay
import dev.nathanpb.dml.compat.rei.widgets.EntityDisplayWidget
import dev.nathanpb.dml.entity.SYSTEM_GLITCH_ENTITY_TYPE
import dev.nathanpb.dml.entity.SystemGlitchEntity
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeCategory
import me.shedaniel.rei.api.widgets.Widgets
import me.shedaniel.rei.gui.widget.Widget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Identifier

class TrialRecipeCategory(private val identifier: Identifier, private val logo: EntryStack) : RecipeCategory<TrialRecipeDisplay> {

    override fun getIdentifier() = identifier

    override fun getLogo() = logo

    override fun getCategoryName(): String = I18n.translate("rei.$MOD_ID.category.trial")

    override fun setupDisplay(recipeDisplay: TrialRecipeDisplay, bounds: Rectangle): MutableList<Widget> {
        val centerX = bounds.centerX - 5
        val centerY = bounds.centerY

        val input = recipeDisplay.inputEntries[0]
        val output = recipeDisplay.outputEntries
            .withIndex()
            .groupBy { (index, _) -> index % 5 }
            .map { it.value.map { it.value } }

        val keySlot = Widgets.createSlot(Point(centerX, centerY - 24)).entries(input)

        val keystoneSlot = Widgets.createSlot(Point(centerX, centerY - 8))
            .entries(mutableListOf(EntryStack.create(BLOCK_TRIAL_KEYSTONE.asItem())))
            .apply {
                isBackgroundEnabled = false
            }

        val outputSlots = output.mapIndexed { index, stacks ->
            val x = (centerX + (index * 18)) - (output.size.dec() * 9)
            Widgets.createSlot(Point(x, centerY + 12)).entries(stacks)
        }

        return mutableListOf<Widget>(
            Widgets.createRecipeBase(bounds),
            *outputSlots.toTypedArray(),
            keySlot,
            keystoneSlot
        ).also { widgets ->
            MinecraftClient.getInstance().player?.let { player ->
                widgets += Widgets.wrapVanillaWidget(EntityDisplayWidget(
                    listOf(player),
                    centerX - 30,
                    centerY + 8,
                    -120F, 20F, 16,
                    ItemStack(Items.DIAMOND_SWORD) // fuck netherite
                ))

                widgets += Widgets.wrapVanillaWidget(EntityDisplayWidget(
                    listOf(SystemGlitchEntity(SYSTEM_GLITCH_ENTITY_TYPE, player.world)),
                    centerX + 45,
                    centerY + 8,
                    120F, 20F, 16
                ))
            }
        }
    }
}
