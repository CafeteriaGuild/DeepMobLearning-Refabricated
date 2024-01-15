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

import com.google.gson.JsonObject
import dev.nathanpb.dml.entityCategory.EntityCategory
import dev.nathanpb.dml.entityCategory.EntityCategoryRegistry
import dev.nathanpb.dml.inventory.LootFabricatorInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.Identifier
import net.minecraft.world.World

/**
 * This is a kinda dummy recipe class made in order to provide compatibility with REI
 */
class LootFabricatorRecipe (
    private val id: Identifier,
    val input: Ingredient,
    val category: EntityCategory
) : Recipe<LootFabricatorInventory> {

    override fun getId() = id

    override fun getType() = RECIPE_LOOT_FABRICATOR

    override fun craft(inv: LootFabricatorInventory, registry: DynamicRegistryManager): ItemStack = ItemStack.EMPTY

    override fun getOutput(registry: DynamicRegistryManager): ItemStack = ItemStack.EMPTY

    override fun fits(width: Int, height: Int) = true

    override fun getSerializer() = LOOT_FABRICATOR_SERIALIZER

    override fun matches(inv: LootFabricatorInventory, world: World?): Boolean {
        return input.test(inv.stackInInputSlot)
    }

    override fun equals(other: Any?): Boolean {
        if (other is LootFabricatorRecipe) {
            return other.category == category
                && other.input.matchingStacks.all { input.test(it) }
                && input.matchingStacks.all { other.input.test(it) }
        }
        return false
    }

    class Serializer : RecipeSerializer<LootFabricatorRecipe> {
        override fun write(buf: PacketByteBuf, recipe: LootFabricatorRecipe) {
            recipe.input.write(buf)
            buf.writeString(recipe.category.id.toString())
        }

        override fun read(id: Identifier, json: JsonObject): LootFabricatorRecipe {
            val input = Ingredient.fromJson(json.get("input"))
            val category = EntityCategoryRegistry.INSTANCE.getOrThrow(
                Identifier(json.getAsJsonPrimitive("category").asString)
            )

            return LootFabricatorRecipe(id, input, category)
        }

        override fun read(id: Identifier, buf: PacketByteBuf): LootFabricatorRecipe {
            val input = Ingredient.fromPacket(buf)
            val category = EntityCategoryRegistry.INSTANCE.getOrThrow(Identifier(buf.readString()))

            return LootFabricatorRecipe(id, input, category)
        }

    }

}
