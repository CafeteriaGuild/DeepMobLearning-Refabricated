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

package dev.nathanpb.dml.enums

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.config
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.*
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.registry.Registry


enum class EntityCategory(val tagKey: TagKey<EntityType<*>>, var exchangeRatio: Int, private val itemForRendering: ()->Item = ::ITEM_DATA_MODEL) {

    OVERWORLD(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("overworld_mobs")), config.lootFabricator.overworldExchangeRatio, ::ITEM_DATA_MODEL_OVERWORLD),
    ZOMBIE(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("zombie_mobs")), config.lootFabricator.zombieExchangeRatio, ::ITEM_DATA_MODEL_ZOMBIE),
    SKELETON(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("skeleton_mobs")), config.lootFabricator.skeletonExchangeRatio, ::ITEM_DATA_MODEL_SKELETON),
    SLIMY(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("slimy_mobs")), config.lootFabricator.slimyExchangeRatio, ::ITEM_DATA_MODEL_SLIMY),
    ILLAGER(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("illager_mobs")), config.lootFabricator.illagerExchangeRatio, ::ITEM_DATA_MODEL_ILLAGER),
    OCEAN(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("ocean_mobs")), config.lootFabricator.oceanExchangeRatio, ::ITEM_DATA_MODEL_OCEAN),
    GHOST(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("ghost_mobs")), config.lootFabricator.ghostExchangeRatio, ::ITEM_DATA_MODEL_GHOST),
    NETHER(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("nether_mobs")), config.lootFabricator.netherExchangeRatio, ::ITEM_DATA_MODEL_NETHER),
    END(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("end_mobs")), config.lootFabricator.endExchangeRatio, ::ITEM_DATA_MODEL_END);


    val displayName = Text.translatable("mobcategory.${MOD_ID}.${tagKey.id.path}")
}
