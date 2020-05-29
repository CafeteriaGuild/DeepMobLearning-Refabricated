package dev.nathanpb.dml.block

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.settings
import net.minecraft.item.BlockItem
import net.minecraft.util.registry.Registry

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

val BLOCK_TRIAL_KEYSTONE = BlockTrialKeystone()

fun registerBlocks() {
    hashMapOf(
        "trial_keystone" to BLOCK_TRIAL_KEYSTONE
    ).forEach { (id, block) ->
        val identifier = identifier(id)
        Registry.register(Registry.BLOCK, identifier, block)
        Registry.register(Registry.ITEM, identifier, BlockItem(block, settings()))
    }
}
