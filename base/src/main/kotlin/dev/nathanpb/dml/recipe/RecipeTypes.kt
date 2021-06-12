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

package dev.nathanpb.dml.recipe

import dev.nathanpb.dml.identifier
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

lateinit var RECIPE_TRIAL_KEY_ATTUNE: RecipeType<TrialKeyAttuneRecipe>
lateinit var RECIPE_TRIAL_KEYSTONE: RecipeType<TrialKeystoneRecipe>
lateinit var RECIPE_CRUSHING: RecipeType<CrushingRecipe>
lateinit var RECIPE_LOOT_FABRICATOR: RecipeType<LootFabricatorRecipe>

private fun <T : Recipe<*>?> register(id: Identifier) = Registry.register(
    Registry.RECIPE_TYPE,
    id,
    object : RecipeType<T> {
        override fun toString() = id.toString()
    })

fun registerRecipeTypes() {
    RECIPE_TRIAL_KEY_ATTUNE = register(identifier("trial_key_attune"))
    RECIPE_TRIAL_KEYSTONE = register(identifier("trial_keystone"))
    RECIPE_CRUSHING = register(identifier("crushing"))
    RECIPE_LOOT_FABRICATOR = register(identifier("loot_fabricator"))
}
