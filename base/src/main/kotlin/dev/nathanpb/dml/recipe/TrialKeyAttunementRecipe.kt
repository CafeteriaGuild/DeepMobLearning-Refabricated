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

import dev.nathanpb.dml.data.DataModelData
import dev.nathanpb.dml.data.TrialKeyData
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.data.trialKeyData
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.item.ItemTrialKey
import dev.nathanpb.dml.utils.items
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class TrialKeyAttuneRecipe (
    private val id: Identifier,
    inputs: DefaultedList<Ingredient>,
    private val output: ItemStack
) : ShapelessRecipe(
    id,
    identifier("trial_key_attune").toString(),
    CraftingRecipeCategory.MISC,
    output,
    inputs
) {

    override fun craft(recipeInputInventory: RecipeInputInventory, dynamicRegistryManager: DynamicRegistryManager): ItemStack = output.copy().apply {
        findDataModel(recipeInputInventory)?.let {
            trialKeyData = TrialKeyData.fromDataModelData(it)
        }
    }

    override fun getId() = id

    override fun getType(): RecipeType<CraftingRecipe> = RecipeType.CRAFTING

    override fun fits(width: Int, height: Int) = true

    override fun getSerializer() = TRIAL_KEY_ATTUNEMENT_SERIALIZER

    override fun getOutput(dynamicRegistryManager: DynamicRegistryManager): ItemStack = output.copy()

    override fun matches(craftingInventory: RecipeInputInventory, world: World): Boolean {
        val dataModel = findDataModel(craftingInventory)
        return super.matches(craftingInventory, world)
                && dataModel != null
                && !hasBoundedTrialKey(craftingInventory)
                && hasTrialKeyRecipe(dataModel, world)
    }

    private fun findDataModel(inv: Inventory) = inv.items().firstOrNull {
        it.item is ItemDataModel && it.dataModel.category != null
    }?.dataModel

    private fun hasBoundedTrialKey(inv: Inventory) = inv.items().any {
        it.item is ItemTrialKey && it.trialKeyData != null
    }

    private fun hasTrialKeyRecipe(model: DataModelData, world: World) : Boolean {
        return world.recipeManager.values()
            .filterIsInstance(TrialKeystoneRecipe::class.java)
            .any {
                model.category == it.category && it.tier == model.tier()
            }
    }
}
