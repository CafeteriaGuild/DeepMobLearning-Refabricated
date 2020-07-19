/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.data

import dev.nathanpb.dml.identifier
import net.minecraft.tag.EntityTypeTags
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier


enum class EntityCategory (val id: Identifier) {
    NETHER(identifier("nether_mobs")),
    SLIMY(identifier("slimy_mobs")),
    OVERWORLD(identifier("overworld_mobs")),
    ZOMBIE(identifier("zombie_mobs")),
    SKELETON(identifier("skeleton_mobs")),
    END(identifier("end_mobs")),
    GHOST(identifier("ghost_mobs")),
    ILLAGER(identifier("illager_mobs")),
    OCEAN(identifier("ocean_mobs"));

    val tag by lazy {
        EntityTypeTags.getContainer().get(id)!!
    }
    val displayName = TranslatableText("mobcategory.deepmoblearning.${id.path}")
}
