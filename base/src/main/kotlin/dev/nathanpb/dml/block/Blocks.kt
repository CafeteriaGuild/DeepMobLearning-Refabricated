/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.block

import dev.nathanpb.dml.identifier
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Rarity

val BLOCK_TRIAL_KEYSTONE = BlockTrialKeystone()
val BLOCK_LOOT_FABRICATOR = BlockLootFabricator()
val BLOCK_CAFETERIA = BlockCafeteria()

fun registerBlocks() {
    hashMapOf(
        BLOCK_TRIAL_KEYSTONE to "trial_keystone",
        BLOCK_LOOT_FABRICATOR to "loot_fabricator",
        BLOCK_CAFETERIA to "cafeteria"
    ).forEach { (block, id) ->
        val identifier = identifier(id)
        Registry.register(Registries.BLOCK, identifier, block)
        Registry.register(Registries.ITEM, identifier, BlockItem(block, FabricItemSettings().rarity(if(block == BLOCK_CAFETERIA) Rarity.EPIC else Rarity.UNCOMMON))) // This is a bad way to do it, but it's fine for now
    }
}
