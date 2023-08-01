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
import dev.nathanpb.dml.item.ITEM_GLITCH_INGOT
import dev.nathanpb.dml.item.ITEM_PHYSICALLY_CONDENSED_MATRIX_FRAGMENT
import dev.nathanpb.dml.item.ITEM_PRISTINE_MATTER_OVERWORLD
import dev.nathanpb.dml.item.ITEM_TRIAL_KEY
import dev.nathanpb.dml.itemgroup.ITEMS
import dev.nathanpb.dml.itemgroup.ITEM_GROUP_KEY
import dev.nathanpb.dml.utils.RarityTuple
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Blocks
import net.minecraft.block.SkullBlock
import net.minecraft.block.WallSkullBlock
import net.minecraft.block.enums.Instrument
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Rarity

val BLOCK_TRIAL_KEYSTONE = BlockTrialKeystone()
val BLOCK_LOOT_FABRICATOR = BlockLootFabricator()
/*val SYSTEM_GLITCH_HEAD = SkullBlock(SkullBlock.Type.CREEPER, FabricBlockSettings.create().instrument(Instrument.CREEPER).strength(1.0f).pistonBehavior(PistonBehavior.DESTROY))
val SYSTEM_GLITCH_WALL_HEAD = WallSkullBlock(SkullBlock.Type.CREEPER, FabricBlockSettings.create().strength(1.0f).dropsLike(SYSTEM_GLITCH_HEAD).pistonBehavior(PistonBehavior.DESTROY))*/
val BLOCK_CAFETERIA = BlockCafeteria()

fun registerBlocks() {
    linkedMapOf(
        BLOCK_TRIAL_KEYSTONE to RarityTuple("trial_keystone", Rarity.UNCOMMON),
        BLOCK_LOOT_FABRICATOR to RarityTuple("loot_fabricator", Rarity.UNCOMMON),
        /*SYSTEM_GLITCH_HEAD to RarityTuple("system_glitch_head", Rarity.EPIC),
        SYSTEM_GLITCH_WALL_HEAD to RarityTuple("system_glitch_wall_head", Rarity.EPIC),*/
        BLOCK_CAFETERIA to RarityTuple("cafeteria", Rarity.EPIC)
    ).forEach { (block, tuple) ->
        val identifier = identifier(tuple.identifier)
        Registry.register(Registries.BLOCK, identifier, block)
        Registry.register(Registries.ITEM, identifier, BlockItem(block, FabricItemSettings().rarity(tuple.rarity)))
    }


    ITEMS.add(0, ItemStack(BLOCK_CAFETERIA))

    ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register {
        it.addAfter(ItemStack(ITEM_TRIAL_KEY), BLOCK_TRIAL_KEYSTONE)
        it.addBefore(ItemStack(ITEM_PRISTINE_MATTER_OVERWORLD), BLOCK_LOOT_FABRICATOR)
    }
}
