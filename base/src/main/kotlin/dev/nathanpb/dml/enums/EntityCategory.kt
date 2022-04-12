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
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.*
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.text.TranslatableText
import net.minecraft.util.registry.Registry


enum class EntityCategory(val tagKey: TagKey<EntityType<*>>, private val itemForRendering: ()->Item = ::ITEM_DATA_MODEL) {
    NETHER(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("nether_mobs")), ::ITEM_DATA_MODEL_NETHER),
    SLIMY(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("slimy_mobs")), ::ITEM_DATA_MODEL_SLIMY),
    OVERWORLD(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("overworld_mobs")), ::ITEM_DATA_MODEL_OVERWORLD),
    ZOMBIE(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("zombie_mobs")), ::ITEM_DATA_MODEL_ZOMBIE),
    SKELETON(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("skeleton_mobs")), ::ITEM_DATA_MODEL_SKELETON),
    END(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("end_mobs")), ::ITEM_DATA_MODEL_END),
    GHOST(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("ghost_mobs")), ::ITEM_DATA_MODEL_GHOST),
    ILLAGER(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("illager_mobs")), ::ITEM_DATA_MODEL_ILLAGER),
    OCEAN(TagKey.of(Registry.ENTITY_TYPE_KEY, identifier("ocean_mobs")), ::ITEM_DATA_MODEL_OCEAN);

    val displayName = TranslatableText("mobcategory.${MOD_ID}.${tagKey.id.path}")
}
