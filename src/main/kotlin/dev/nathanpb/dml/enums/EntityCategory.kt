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
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tag.EntityTypeTags
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier


enum class EntityCategory (val id: Identifier, private val itemForRendering: ()->Item = ::ITEM_DATA_MODEL) {
    NETHER(identifier("nether_mobs"), ::ITEM_DATA_MODEL_NETHER),
    SLIMY(identifier("slimy_mobs"), ::ITEM_DATA_MODEL_SLIMY),
    OVERWORLD(identifier("overworld_mobs"), ::ITEM_DATA_MODEL_OVERWORLD),
    ZOMBIE(identifier("zombie_mobs"), ::ITEM_DATA_MODEL_ZOMBIE),
    SKELETON(identifier("skeleton_mobs"), ::ITEM_DATA_MODEL_SKELETON),
    END(identifier("end_mobs"), ::ITEM_DATA_MODEL_END),
    GHOST(identifier("ghost_mobs"), ::ITEM_DATA_MODEL_GHOST),
    ILLAGER(identifier("illager_mobs"), ::ITEM_DATA_MODEL_ILLAGER),
    OCEAN(identifier("ocean_mobs"), ::ITEM_DATA_MODEL_OCEAN);

    val tag by lazy {
        EntityTypeTags.getTagGroup().getTag(id)!!
    }
    val displayName = TranslatableText("mobcategory.${MOD_ID}.${id.path}")

    fun stackForRendering(tier: DataModelTier): ItemStack {
        return ItemStack(itemForRendering()).apply {
            dataModel.dataAmount = tier.dataAmount
        }
    }
}
