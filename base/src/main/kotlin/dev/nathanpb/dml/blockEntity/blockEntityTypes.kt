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

package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.block.*
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import team.reborn.energy.api.EnergyStorage


lateinit var BLOCKENTITY_TRIAL_KEYSTONE: BlockEntityType<BlockEntityTrialKeystone>
lateinit var BLOCKENTITY_DATA_SYNTHESIZER: BlockEntityType<BlockEntityDataSynthesizer>
lateinit var BLOCKENTITY_LOOT_FABRICATOR: BlockEntityType<BlockEntityLootFabricator>
lateinit var BLOCKENTITY_DISRUPTIONS_CORE: BlockEntityType<BlockEntityDisruptionsCore>
lateinit var BLOCKENTITY_FADING_GLITCHED_TILE: BlockEntityType<BlockEntityFadingGlitchedTile>

private fun <E: BlockEntity, B: Block> register(block: B, builder: (BlockPos, BlockState)->E) = Registry.register(
    Registries.BLOCK_ENTITY_TYPE,
    Registries.BLOCK.getId(block),
    BlockEntityType.Builder.create(builder, block).build(null)
)

fun registerBlockEntityTypes() {
    BLOCKENTITY_TRIAL_KEYSTONE = register(BLOCK_TRIAL_KEYSTONE, ::BlockEntityTrialKeystone)
    BLOCKENTITY_DATA_SYNTHESIZER = register(BLOCK_DATA_SYNTHESIZER, ::BlockEntityDataSynthesizer).also {
        EnergyStorage.SIDED.registerForBlockEntity(
            { blockEntity, direction ->
                if(blockEntity.cachedState[Properties.HORIZONTAL_FACING] == direction) {
                    return@registerForBlockEntity null
                }
                return@registerForBlockEntity blockEntity.energyStorage
            },
            it
        )
    }
    BLOCKENTITY_LOOT_FABRICATOR = register(BLOCK_LOOT_FABRICATOR, ::BlockEntityLootFabricator).also {
        EnergyStorage.SIDED.registerForBlockEntity(
            { blockEntity, _ -> blockEntity.energyStorage },
            it
        )
    }

    BLOCKENTITY_DISRUPTIONS_CORE = register(BLOCK_DISRUPTIONS_CORE, ::BlockEntityDisruptionsCore)
    BLOCKENTITY_FADING_GLITCHED_TILE = register(BLOCK_FADING_GLITCHED_TILE, ::BlockEntityFadingGlitchedTile)
}
