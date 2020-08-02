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
import dev.nathanpb.dml.compat.rei.display.LootFabricatorRecipeDisplay
import dev.nathanpb.dml.compat.rei.widgets.EntityDisplayWidget
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeCategory
import me.shedaniel.rei.api.widgets.Widgets
import me.shedaniel.rei.gui.widget.Widget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier

class LootFabricatorRecipeCategory(private val identifier: Identifier, private val logo: EntryStack) : RecipeCategory<LootFabricatorRecipeDisplay> {

    override fun getIdentifier() = identifier

    override fun getLogo() = logo

    override fun getCategoryName(): String = I18n.translate("rei.$MOD_ID.category.loot_fabricator")

    override fun setupDisplay(recipeDisplay: LootFabricatorRecipeDisplay, bounds: Rectangle): MutableList<Widget> {
        val centerX = bounds.centerX - 8
        val centerY = bounds.centerY - 8

        val entities = recipeDisplay.recipe.category.tag.values()

        return mutableListOf<Widget>(
            Widgets.createRecipeBase(bounds),
            Widgets.createSlot(Point(centerX - 40, centerY)).entries(recipeDisplay.inputEntries.flatten()),
            Widgets.createArrow(Point(centerX - 10, centerY))
        ).also { widgets ->
            MinecraftClient.getInstance().player?.let { player ->
                widgets += Widgets.wrapVanillaWidget(EntityDisplayWidget(
                    entities.mapNotNull { it.create(player.world) as? LivingEntity },
                    centerX + 40,
                    centerY + 30,
                    0F, 0F, 24
                ))
            }
        }
    }
}
