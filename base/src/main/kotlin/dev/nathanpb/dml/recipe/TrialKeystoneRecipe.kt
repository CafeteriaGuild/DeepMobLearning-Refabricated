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
import dev.nathanpb.dml.data.TrialKeyData
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.item.ITEM_GLITCH_UPGRADE_SMITHING_TEMPLATE
import dev.nathanpb.dml.utils.MODULAR_ARMOR_ID
import dev.nathanpb.dml.utils.isModLoaded
import dev.nathanpb.dml.utils.takeOrNull
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.Identifier
import net.minecraft.world.World
import kotlin.random.Random

class TrialKeystoneRecipe (
    private val id: Identifier,
    val category: EntityCategory,
    val tier: DataModelTier,
    val spawnRate: Map<Regex, Float>,
    private val rewards: List<ItemStack>,
    waveEntityCount: Int?,
    waveRespawnTimeout: Int?
    ) : Recipe<SimpleInventory> {

    val waveEntityCount = waveEntityCount ?: tier.defaultWaveEntityCount
    val waveRespawnTimeout = waveRespawnTimeout ?: tier.defaultWaveRespawnTimeout

    companion object {
        fun findOrNull(world: World, data: TrialKeyData) = world.recipeManager.values()
            .filterIsInstance(TrialKeystoneRecipe::class.java)
            .firstOrNull { it.category == data.category && it.tier == data.tier() }
    }

    fun copyRewards(): MutableList<ItemStack> {
        return copyRewards(false)
    }

    fun copyRewards(onREI: Boolean): MutableList<ItemStack> {
        val rewardsCopy = rewards.map(ItemStack::copy).toMutableList()

        // Glitch Upgrade
        if(isModLoaded(MODULAR_ARMOR_ID)) {
            if(tier.glitchUpgradeOdds > 0 && (Random.nextDouble() < tier.glitchUpgradeOdds || onREI)) {
                rewardsCopy.add(ItemStack(ITEM_GLITCH_UPGRADE_SMITHING_TEMPLATE))
            }
        }

        // TODO System Glitch Head
        /*
        if(tier == DataModelTier.SELF_AWARE) {
            if((Random.nextDouble() < 0.15 || onREI)) {
                //rewardsCopy.add(ItemStack(SYSTEM_GLITCH_HEAD))
            }
        }*/

        return rewardsCopy
    }

    @Deprecated("", ReplaceWith("copyRewards", "dev.nathanpb.dml.recipe"))
    override fun craft(inv: SimpleInventory, registry: DynamicRegistryManager): ItemStack = ItemStack.EMPTY

    @Deprecated("", ReplaceWith("copyRewards", "dev.nathanpb.dml.recipe"))
    override fun getOutput(registry: DynamicRegistryManager): ItemStack = ItemStack.EMPTY

    override fun getId() = id

    override fun getType() = RECIPE_TRIAL_KEYSTONE

    override fun fits(width: Int, height: Int) = true

    override fun getSerializer() = TRIAL_KEYSTONE_RECIPE_SERIALIZER

    override fun matches(inv: SimpleInventory?, world: World?): Boolean {
        return false
    }

    class Serializer : RecipeSerializer<TrialKeystoneRecipe> {
        override fun write(buf: PacketByteBuf, recipe: TrialKeystoneRecipe) {
            buf.writeString(recipe.category.name)
            buf.writeInt(recipe.tier.ordinal)
            buf.writeInt(recipe.waveEntityCount)
            buf.writeInt(recipe.waveRespawnTimeout)
            buf.writeInt(recipe.rewards.size)
            recipe.rewards.forEach { buf.writeItemStack(it) }
            buf.writeInt(recipe.spawnRate.size)
            recipe.spawnRate.forEach { (k, v) ->
                buf.writeString(k.toString())
                buf.writeFloat(v)
            }
        }

        override fun read(id: Identifier, json: JsonObject): TrialKeystoneRecipe {
            val tier = DataModelTier.fromIndex(json.getAsJsonPrimitive("tier").asInt) ?: DataModelTier.FAULTY
            val category = EntityCategory.valueOf(json.getAsJsonPrimitive("category").asString)
            val rewards = json.getAsJsonArray("rewards").map {
                ShapedRecipe.outputFromJson(it.asJsonObject)
            }

            val waveEntityCount = takeOrNull(json.has("waveEntityCount")) {
                json.getAsJsonPrimitive("waveEntityCount").asInt
            }

            val waveRespawnTimeout = takeOrNull(json.has("waveRespawnTimeout")) {
                json.getAsJsonPrimitive("waveRespawnTimeout").asInt
            }

            val rates = json.getAsJsonObject("spawnRates").let { obj ->
                val sum = obj.entrySet().sumOf { it.value.asInt }.toFloat()
                obj.entrySet()
                    .asSequence()
                    .map { (k, v) -> k.toRegex() to v.asInt.toFloat() }
                    .map { (k, v) -> k to (v * 1f) / sum }
                    .toMap()
            }

            return TrialKeystoneRecipe(
                id,
                category,
                tier,
                rates,
                rewards,
                waveEntityCount,
                waveRespawnTimeout
            )
        }

        override fun read(id: Identifier, buf: PacketByteBuf): TrialKeystoneRecipe {
            val category = EntityCategory.valueOf(buf.readString())
            val tier = DataModelTier.fromIndex(buf.readInt()) ?: DataModelTier.FAULTY
            val waveCount = buf.readInt()
            val waveRespawnTimeout = buf.readInt()
            val stacks = (1 .. buf.readInt()).map {
                buf.readItemStack()
            }

            val spawnRates = (1..buf.readInt()).associate {
                buf.readString().toRegex() to buf.readFloat()
            }

            return TrialKeystoneRecipe(id, category, tier, spawnRates, stacks, waveCount, waveRespawnTimeout)
        }
    }
}
