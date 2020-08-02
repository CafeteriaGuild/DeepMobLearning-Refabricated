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

package dev.nathanpb.dml.compat.rei

import dev.nathanpb.dml.block.BLOCK_LOOT_FABRICATOR
import dev.nathanpb.dml.compat.rei.category.CrushingRecipeCategory
import dev.nathanpb.dml.compat.rei.category.LootFabricatorRecipeCategory
import dev.nathanpb.dml.compat.rei.category.TrialRecipeCategory
import dev.nathanpb.dml.compat.rei.display.CrushingRecipeDisplay
import dev.nathanpb.dml.compat.rei.display.LootFabricatorRecipeDisplay
import dev.nathanpb.dml.compat.rei.display.TrialRecipeDisplay
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ITEM_DML
import dev.nathanpb.dml.item.ITEM_EMERITUS_HAT
import dev.nathanpb.dml.item.ITEM_SOOT_REDSTONE
import dev.nathanpb.dml.item.ITEM_TRIAL_KEY
import dev.nathanpb.dml.recipe.CrushingRecipe
import dev.nathanpb.dml.recipe.LootFabricatorRecipe
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import me.shedaniel.rei.api.EntryRegistry
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeHelper
import me.shedaniel.rei.api.plugins.REIPluginV0

@Suppress("unused")
class ReiPlugin : REIPluginV0 {

    companion object {
        val CRUSHING_ID = identifier("crushing")
        val TRIAL_ID = identifier("trial")
        val LOOT_FABRICATOR_ID = identifier("loot_fabricator")

        val CRUSHING_LOGO: EntryStack = EntryStack.create(ITEM_SOOT_REDSTONE)
        val TRIAL_LOGO: EntryStack = EntryStack.create(ITEM_TRIAL_KEY)
        val LOOT_FABRICATOR_LOGO: EntryStack = EntryStack.create(BLOCK_LOOT_FABRICATOR.asItem())
    }

    override fun getPluginIdentifier() = identifier("rei_compat")

    override fun registerPluginCategories(recipeHelper: RecipeHelper?) {
        recipeHelper?.registerCategory(CrushingRecipeCategory(CRUSHING_ID, CRUSHING_LOGO))
        recipeHelper?.registerCategory(TrialRecipeCategory(TRIAL_ID, TRIAL_LOGO))
        recipeHelper?.registerCategory(LootFabricatorRecipeCategory(LOOT_FABRICATOR_ID, LOOT_FABRICATOR_LOGO))
    }

    override fun registerRecipeDisplays(recipeHelper: RecipeHelper?) {
        recipeHelper?.registerRecipes(CRUSHING_ID, CrushingRecipe::class.java) {
            CrushingRecipeDisplay(it, CRUSHING_ID)
        }

        recipeHelper?.registerRecipes(TRIAL_ID, TrialKeystoneRecipe::class.java){
            TrialRecipeDisplay(TRIAL_ID, it)
        }

        recipeHelper?.registerRecipes(LOOT_FABRICATOR_ID, LootFabricatorRecipe::class.java) {
            LootFabricatorRecipeDisplay(LOOT_FABRICATOR_ID, it)
        }
    }

    override fun postRegister() {
        val hiddenItems = listOf(ITEM_EMERITUS_HAT, ITEM_DML)
        EntryRegistry.getInstance().removeEntryIf {
            it.item in hiddenItems
        }
    }


    override fun registerOthers(recipeHelper: RecipeHelper?) {
        recipeHelper?.removeAutoCraftButton(CRUSHING_ID)
        recipeHelper?.removeAutoCraftButton(TRIAL_ID)
        recipeHelper?.removeAutoCraftButton(LOOT_FABRICATOR_ID)
        recipeHelper?.registerWorkingStations(CRUSHING_ID, CRUSHING_LOGO)
        recipeHelper?.registerWorkingStations(TRIAL_ID, TRIAL_LOGO)
        recipeHelper?.registerWorkingStations(LOOT_FABRICATOR_ID, LOOT_FABRICATOR_LOGO)
    }
}
