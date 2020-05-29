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
import dev.nathanpb.dml.inventory.TrialKeystoneInventory
import net.minecraft.entity.EntityType
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

class TrialKeystoneRecipe (
    private val id: Identifier,
    val entity: EntityType<*>,
    val tier: DataModelTier,
    val waves: List<Int>,
    private val rewards: List<ItemStack>
) : Recipe<TrialKeystoneInventory> {

    fun craftMultiple(inv: TrialKeystoneInventory) = rewards.map {
        it.copy()
    }

    fun getOutputMultiple() = rewards

    @Deprecated("", ReplaceWith("craftMultiple", "dev.nathanpb.dml.recipe"))
    override fun craft(inv: TrialKeystoneInventory?) = ItemStack.EMPTY

    @Deprecated("", ReplaceWith("getOutputMultiple", "dev.nathanpb.dml.recipe"))
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
            buf.writeString(Registry.ENTITY_TYPE.getId(recipe.entity).toString())
            buf.writeInt(recipe.tier.ordinal)
            buf.writeIntArray(recipe.waves.toIntArray())
            buf.writeInt(recipe.rewards.size)
            recipe.rewards.forEach { buf.writeItemStack(it) }
        }

        override fun read(id: Identifier, json: JsonObject): TrialKeystoneRecipe {
            return TrialKeystoneRecipe(
                id,
                Registry.ENTITY_TYPE[Identifier(json.getAsJsonPrimitive("entity").asString)],
                DataModelTier.fromIndex(json.getAsJsonPrimitive("tier").asInt) ?: DataModelTier.FAULTY,
                json.getAsJsonArray("waves").map { it.asInt },
                json.getAsJsonArray("rewards").map {
                    ShapedRecipe.getItemStack(it.asJsonObject)
                }
            )
        }

        override fun read(id: Identifier, buf: PacketByteBuf): TrialKeystoneRecipe {
            val entity = Registry.ENTITY_TYPE[Identifier(buf.readString())]
            val tier = DataModelTier.fromIndex(buf.readInt()) ?: DataModelTier.FAULTY
            val waves = buf.readIntArray()
            val stacks = (1 .. buf.readInt()).map {
                buf.readItemStack()
            }

            return TrialKeystoneRecipe(id, entity, tier, waves.toList(), stacks)
        }
    }
}
