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
import dev.nathanpb.dml.compat.rei.category.CrushingDisplayCategory
import dev.nathanpb.dml.compat.rei.category.LootFabricatorDisplayCategory
import dev.nathanpb.dml.compat.rei.category.TrialDisplayCategory
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
import dev.nathanpb.dml.utils.itemStack
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry
import me.shedaniel.rei.api.common.util.EntryStacks


@Suppress("unused")
class ReiPlugin :  REIClientPlugin {

    companion object {
        val CRUSHING_ID = identifier("crushing")
        val TRIAL_ID = identifier("trial")
        val LOOT_FABRICATOR_ID = identifier("loot_fabricator")

    }

    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(
            CrushingDisplayCategory(CRUSHING_ID, EntryStacks.of(ITEM_SOOT_REDSTONE)),
            TrialDisplayCategory(TRIAL_ID, EntryStacks.of(ITEM_TRIAL_KEY)),
            LootFabricatorDisplayCategory(LOOT_FABRICATOR_ID, EntryStacks.of(BLOCK_LOOT_FABRICATOR.asItem()))
        )
    }

    override fun registerDisplays(registry: DisplayRegistry){
        registry.registerFiller(CrushingRecipe::class.java) {
            CrushingRecipeDisplay(it, CRUSHING_ID)
        }

        registry.registerFiller(TrialKeystoneRecipe::class.java){
            TrialRecipeDisplay(TRIAL_ID, it)
        }


        registry.registerFiller(LootFabricatorRecipe::class.java) {
            LootFabricatorRecipeDisplay(LOOT_FABRICATOR_ID, it)
        }
    }

    override fun postRegister() {
        val hiddenItems = listOf(ITEM_EMERITUS_HAT, ITEM_DML)
        EntryRegistry.getInstance().removeEntryIf {
            it.itemStack().item in hiddenItems
        }
    }

    /*
    override fun registerOthers(recipeHelper: RecipeHelper?) {
        recipeHelper?.removeAutoCraftButton(CRUSHING_ID)
        recipeHelper?.removeAutoCraftButton(TRIAL_ID)
        recipeHelper?.removeAutoCraftButton(LOOT_FABRICATOR_ID)
        recipeHelper?.registerWorkingStations(CRUSHING_ID, CRUSHING_LOGO)
        recipeHelper?.registerWorkingStations(TRIAL_ID, TRIAL_LOGO)
        recipeHelper?.registerWorkingStations(LOOT_FABRICATOR_ID, LOOT_FABRICATOR_LOGO)
    }
    */
}
