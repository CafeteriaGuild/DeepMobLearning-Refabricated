/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.recipe

import com.google.gson.JsonObject
import dev.nathanpb.dml.data.DataModelTier
import dev.nathanpb.dml.data.EntityCategory
import dev.nathanpb.dml.data.TrialKeyData
import dev.nathanpb.dml.inventory.TrialKeystoneInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.world.World

class TrialKeystoneRecipe (
    private val id: Identifier,
    val category: EntityCategory,
    val tier: DataModelTier,
    val waves: List<Int>,
    private val rewards: List<ItemStack>
) : Recipe<TrialKeystoneInventory> {

    companion object {
        fun findOrNull(world: World, data: TrialKeyData) = world.recipeManager.values()
            .filterIsInstance(TrialKeystoneRecipe::class.java)
            .firstOrNull { it.category == data.category && it.tier == data.tier() }
    }

    fun copyRewards() = rewards.map(ItemStack::copy)

    @Deprecated("", ReplaceWith("copyRewards", "dev.nathanpb.dml.recipe"))
    override fun craft(inv: TrialKeystoneInventory?) = ItemStack.EMPTY

    @Deprecated("", ReplaceWith("copyRewards", "dev.nathanpb.dml.recipe"))
    override fun getOutput() = ItemStack.EMPTY

    override fun getId() = id

    override fun getType() = RECIPE_TRIAL_KEYSTONE

    override fun fits(width: Int, height: Int) = true

    override fun getSerializer() = TRIAL_KEYSTONE_RECIPE_SERIALIZER



    override fun matches(inv: TrialKeystoneInventory?, world: World?): Boolean {
        TODO("Not yet implemented")
    }

    class Serializer : RecipeSerializer<TrialKeystoneRecipe> {
        override fun write(buf: PacketByteBuf, recipe: TrialKeystoneRecipe) {
            buf.writeString(recipe.category.name)
            buf.writeInt(recipe.tier.ordinal)
            buf.writeIntArray(recipe.waves.toIntArray())
            buf.writeInt(recipe.rewards.size)
            recipe.rewards.forEach { buf.writeItemStack(it) }
        }

        override fun read(id: Identifier, json: JsonObject): TrialKeystoneRecipe {
            val tier = DataModelTier.fromIndex(json.getAsJsonPrimitive("tier").asInt) ?: DataModelTier.FAULTY
            return TrialKeystoneRecipe(
                id,
                EntityCategory.valueOf(json.getAsJsonPrimitive("category").asString),
                tier,
                json.getAsJsonArray("waves")?.map { it.asInt } ?: tier.defaultWave,
                json.getAsJsonArray("rewards").map {
                    ShapedRecipe.getItemStack(it.asJsonObject)
                }
            )
        }

        override fun read(id: Identifier, buf: PacketByteBuf): TrialKeystoneRecipe {
            val category = EntityCategory.valueOf(buf.readString())
            val tier = DataModelTier.fromIndex(buf.readInt()) ?: DataModelTier.FAULTY
            val waves = buf.readIntArray()
            val stacks = (1 .. buf.readInt()).map {
                buf.readItemStack()
            }

            return TrialKeystoneRecipe(id, category, tier, waves.toList(), stacks)
        }
    }
}
