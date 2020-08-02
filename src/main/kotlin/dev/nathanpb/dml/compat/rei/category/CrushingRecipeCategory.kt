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

package dev.nathanpb.dml.compat.rei.category

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.compat.rei.display.CrushingRecipeDisplay
import dev.nathanpb.dml.compat.rei.widgets.EntityDisplayWidget
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeCategory
import me.shedaniel.rei.api.widgets.Widgets
import me.shedaniel.rei.gui.widget.Widget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

class CrushingRecipeCategory(private val identifier: Identifier, private val logo: EntryStack) : RecipeCategory<CrushingRecipeDisplay> {

    override fun getIdentifier() = identifier

    override fun getLogo() = logo

    override fun getCategoryName(): String = I18n.translate("rei.${MOD_ID}.category.crushing")

    override fun setupDisplay(recipeDisplay: CrushingRecipeDisplay, bounds: Rectangle): MutableList<Widget> {
        val startPoint = Point(bounds.centerX - 41, bounds.centerY - 27)

        val input = recipeDisplay.inputEntries[0]
        val block = recipeDisplay.inputEntries[1]
        val output = recipeDisplay.outputEntries

        val blockSlot = Widgets.createSlot(Point(startPoint.x, startPoint.y + 38)).entries(block).apply {
            isBackgroundEnabled = false
        }

        return mutableListOf<Widget>(
            Widgets.createRecipeBase(bounds),
            Widgets.createArrow(Point(startPoint.x + 35, startPoint.y + 38)),
            Widgets.createSlot(Point(startPoint.x - 20, startPoint.y + 38)).entries(input),
            blockSlot,
            Widgets.createSlot(Point(startPoint.x + 80, startPoint.y + 38)).entries(output)
        ).also { widgets ->
            MinecraftClient.getInstance().player?.let { player ->
                EntityDisplayWidget(
                    listOf(player),
                    startPoint.x + 24,
                    startPoint.y + 52,
                    180F,
                    -120F,
                    16,
                    recipeDisplay.inputEntries.first().random().itemStack
                ) { it.swingHand(Hand.MAIN_HAND) }.let {
                    widgets += Widgets.wrapVanillaWidget(it)
                }
            }
        }
    }

}
