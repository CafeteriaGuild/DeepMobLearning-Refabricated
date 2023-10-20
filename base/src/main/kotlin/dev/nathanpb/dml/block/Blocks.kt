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
import dev.nathanpb.dml.utils.RarityTuple
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Rarity

val BLOCK_TRIAL_KEYSTONE = BlockTrialKeystone()
val BLOCK_DATA_SYNTHESIZER = BlockDataSynthesizer()
val BLOCK_LOOT_FABRICATOR = BlockLootFabricator()
/*val SYSTEM_GLITCH_HEAD = SkullBlock(SkullBlock.Type.CREEPER, FabricBlockSettings.create().instrument(Instrument.CREEPER).strength(1.0f).pistonBehavior(PistonBehavior.DESTROY))
val SYSTEM_GLITCH_WALL_HEAD = WallSkullBlock(SkullBlock.Type.CREEPER, FabricBlockSettings.create().strength(1.0f).dropsLike(SYSTEM_GLITCH_HEAD).pistonBehavior(PistonBehavior.DESTROY))*/
val BLOCK_DISRUPTIONS_CORE = BlockDisruptionsCore()
val BLOCK_FADING_GLITCHED_TILE = BlockFadingGlitchedTile()
val BLOCK_GLITCHED_TILE = Block(FabricBlockSettings.copy(Blocks.STONE).mapColor(MapColor.CYAN))
val BLOCK_GLITCHED_STAIRS = StairsBlock(BLOCK_GLITCHED_TILE.defaultState, FabricBlockSettings.copy(BLOCK_GLITCHED_TILE))
val BLOCK_GLITCHED_SLAB = SlabBlock(FabricBlockSettings.copy(BLOCK_GLITCHED_TILE))
val BLOCK_GLITCHED_WALL = WallBlock(FabricBlockSettings.copy(BLOCK_GLITCHED_TILE).solid())
val BLOCK_CAFETERIA = BlockCafeteria()

fun registerBlocks() {
    linkedMapOf(
        BLOCK_TRIAL_KEYSTONE to RarityTuple("trial_keystone", Rarity.UNCOMMON),
        BLOCK_DATA_SYNTHESIZER to RarityTuple("data_synthesizer", Rarity.UNCOMMON),
        BLOCK_LOOT_FABRICATOR to RarityTuple("loot_fabricator", Rarity.UNCOMMON),
        /*SYSTEM_GLITCH_HEAD to RarityTuple("system_glitch_head", Rarity.EPIC),
        SYSTEM_GLITCH_WALL_HEAD to RarityTuple("system_glitch_wall_head", Rarity.EPIC),*/
        BLOCK_DISRUPTIONS_CORE to RarityTuple("disruptions_core", Rarity.UNCOMMON),
        BLOCK_FADING_GLITCHED_TILE to RarityTuple("fading_glitched_tile"),
        BLOCK_GLITCHED_TILE to RarityTuple("glitched_tile"),
        BLOCK_GLITCHED_STAIRS to RarityTuple("glitched_stairs"),
        BLOCK_GLITCHED_SLAB to RarityTuple("glitched_slab"),
        BLOCK_GLITCHED_WALL to RarityTuple("glitched_wall"),
        BLOCK_CAFETERIA to RarityTuple("cafeteria", Rarity.EPIC)
    ).forEach { (block, tuple) ->
        val identifier = identifier(tuple.identifier)
        Registry.register(Registries.BLOCK, identifier, block)
        Registry.register(Registries.ITEM, identifier, BlockItem(block, FabricItemSettings().rarity(tuple.rarity)))
    }

}