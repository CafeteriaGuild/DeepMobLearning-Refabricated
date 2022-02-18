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
import dev.nathanpb.dml.compat.rei.ReiPlugin
import dev.nathanpb.dml.compat.rei.display.CrushingRecipeDisplay
import dev.nathanpb.dml.compat.rei.widgets.EntityDisplayWidget
import dev.nathanpb.dml.compat.rei.itemStack
import dev.nathanpb.dml.item.ITEM_SOOT_REDSTONE
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

class CrushingDisplayCategory: DisplayCategory<CrushingRecipeDisplay> {

    override fun getIdentifier(): Identifier = ReiPlugin.CRUSHING_CATEGORY.identifier

    override fun getCategoryIdentifier(): CategoryIdentifier<out CrushingRecipeDisplay> = ReiPlugin.CRUSHING_CATEGORY

    override fun getIcon(): EntryStack<ItemStack> = EntryStacks.of(ITEM_SOOT_REDSTONE)

    override fun getTitle() = TranslatableText("rei.${MOD_ID}.category.crushing")


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
            Widgets.createSlot(Point(startPoint.x + 80, startPoint.y + 38)).entries(output.flatten())
        ).also { widgets ->
            MinecraftClient.getInstance().player?.let { player ->
                EntityDisplayWidget(
                    listOf(player),
                    startPoint.x + 24,
                    startPoint.y + 52,
                    180F,
                    -120F,
                    16,
                    recipeDisplay.inputEntries.flatten().random().itemStack()
                ) { it.swingHand(Hand.MAIN_HAND) }.let {
                    widgets += Widgets.wrapVanillaWidget(it)
                }
            }
        }
    }

}
