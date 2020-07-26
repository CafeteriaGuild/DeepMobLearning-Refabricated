/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.compat.rei.display

import dev.nathanpb.dml.data.TrialKeyData
import dev.nathanpb.dml.data.trialKeyData
import dev.nathanpb.dml.item.ITEM_TRIAL_KEY
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeDisplay
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

class TrialRecipeDisplay (
    private val categoryId: Identifier,
    recipe: TrialKeystoneRecipe
) : RecipeDisplay {

    private val input = mutableListOf(
        mutableListOf(EntryStack.create(
            ItemStack(ITEM_TRIAL_KEY).apply {
                trialKeyData = TrialKeyData(recipe.category, recipe.tier.dataAmount)
            }
        ))
    )

    private val output = recipe.copyRewards().map(EntryStack::create)

    override fun getRecipeCategory() = categoryId

    override fun getInputEntries() = input

    override fun getOutputEntries() = output

}
