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
import dev.nathanpb.dml.block.BLOCK_LOOT_FABRICATOR
import dev.nathanpb.dml.compat.rei.ReiPlugin
import dev.nathanpb.dml.compat.rei.display.LootFabricatorRecipeDisplay
import dev.nathanpb.dml.compat.rei.widgets.EntityDisplayWidget
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.util.EntryStacks
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class LootFabricatorDisplayCategory: DisplayCategory<LootFabricatorRecipeDisplay> {

    override fun getIdentifier(): Identifier = ReiPlugin.LOOT_FABRICATOR_CATEGORY.identifier

    override fun getCategoryIdentifier(): CategoryIdentifier<out LootFabricatorRecipeDisplay> = ReiPlugin.LOOT_FABRICATOR_CATEGORY

    override fun getIcon(): EntryStack<ItemStack> = EntryStacks.of(BLOCK_LOOT_FABRICATOR)

    override fun getTitle(): MutableText = Text.translatable("rei.$MOD_ID.category.loot_fabricator")


    override fun setupDisplay(recipeDisplay: LootFabricatorRecipeDisplay, bounds: Rectangle): MutableList<Widget> {
        val centerX = bounds.centerX - 8
        val centerY = bounds.centerY - 8

        val entities: MutableList<EntityType<*>> = arrayListOf()
        Registry.ENTITY_TYPE.iterateEntries(recipeDisplay.recipe.category.tagKey).forEach {
            entities.add(it.value())
        }

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
