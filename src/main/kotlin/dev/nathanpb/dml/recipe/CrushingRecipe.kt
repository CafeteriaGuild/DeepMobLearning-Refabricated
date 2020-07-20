/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.recipe

import com.google.gson.JsonObject
import dev.nathanpb.dml.inventory.CrushingRecipeTempInventory
import dev.nathanpb.dml.utils.items
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

class CrushingRecipe (
    private val id: Identifier,
    val input: Ingredient,
    val block: Block,
    private val output: ItemStack
) : Recipe<CrushingRecipeTempInventory> {

    override fun getId() = id
    override fun getType() = RECIPE_CRUSHING
    override fun fits(width: Int, height: Int) = true
    override fun getSerializer() = CRUSHING_RECIPE_SERIALIZER
    override fun getOutput() = output

    override fun craft(inv: CrushingRecipeTempInventory): ItemStack {
        return getOutput().copy().also {
            inv.items().firstOrNull {
                input.test(it)
            }?.decrement(1)
        }
    }

    override fun matches(inv: CrushingRecipeTempInventory, world: World) = inv.items().any {
        input.test(it)
    }

    class Serializer : RecipeSerializer<CrushingRecipe> {
        override fun write(buf: PacketByteBuf, recipe: CrushingRecipe) {
            recipe.input.write(buf)
            buf.writeIdentifier(Registry.BLOCK.getId(recipe.block))
            buf.writeItemStack(recipe.output)
        }

        override fun read(id: Identifier, buf: PacketByteBuf): CrushingRecipe {
            return CrushingRecipe(
                id,
                Ingredient.fromPacket(buf),
                Registry.BLOCK[buf.readIdentifier()],
                buf.readItemStack()
            )
        }

        override fun read(id: Identifier, json: JsonObject): CrushingRecipe {
            return CrushingRecipe(
                id,
                Ingredient.fromJson(json.getAsJsonObject("input")),
                Registry.BLOCK[Identifier(json.getAsJsonPrimitive("block").asString)],
                ShapedRecipe.getItemStack(json.getAsJsonObject("output"))
            )
        }
    }
}
