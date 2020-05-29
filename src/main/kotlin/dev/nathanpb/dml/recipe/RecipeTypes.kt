package dev.nathanpb.dml.recipe

import dev.nathanpb.dml.identifier
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

lateinit var RECIPE_TRIAL_KEY_ATTUNE: RecipeType<TrialKeyAttuneRecipe>
lateinit var RECIPE_TRIAL_KEYSTONE: RecipeType<TrialKeystoneRecipe>

private fun <T : Recipe<*>?> register(id: Identifier) = Registry.register(
    Registry.RECIPE_TYPE,
    id,
    object : RecipeType<T> {
        override fun toString() = id.toString()
    })

fun registerRecipeTypes() {
    RECIPE_TRIAL_KEY_ATTUNE = register(identifier("trial_key_attune"))
    RECIPE_TRIAL_KEYSTONE = register(identifier("trial_keystone"))
}
