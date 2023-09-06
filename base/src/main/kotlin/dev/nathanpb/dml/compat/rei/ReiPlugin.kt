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

package dev.nathanpb.dml.compat.rei

import dev.nathanpb.dml.block.BLOCK_LOOT_FABRICATOR
import dev.nathanpb.dml.block.BLOCK_TRIAL_KEYSTONE
import dev.nathanpb.dml.compat.rei.category.CrushingDisplayCategory
import dev.nathanpb.dml.compat.rei.category.LootFabricatorDisplayCategory
import dev.nathanpb.dml.compat.rei.category.TrialDisplayCategory
import dev.nathanpb.dml.compat.rei.display.CrushingRecipeDisplay
import dev.nathanpb.dml.compat.rei.display.LootFabricatorRecipeDisplay
import dev.nathanpb.dml.compat.rei.display.TrialRecipeDisplay
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.ITEM_DML
import dev.nathanpb.dml.item.ITEM_EMERITUS_HAT
import dev.nathanpb.dml.item.ITEM_GLITCH_UPGRADE_SMITHING_TEMPLATE
import dev.nathanpb.dml.recipe.CrushingRecipe
import dev.nathanpb.dml.recipe.LootFabricatorRecipe
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import dev.nathanpb.dml.utils.MODULAR_ARMOR_ID
import dev.nathanpb.dml.utils.isModLoaded
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry
import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.plugins.PluginManager
import me.shedaniel.rei.api.common.registry.ReloadStage
import me.shedaniel.rei.api.common.util.EntryStacks


@Suppress("unused")
class ReiPlugin :  REIClientPlugin {

    companion object {
        val CRUSHING_CATEGORY: CategoryIdentifier<CrushingRecipeDisplay> = CategoryIdentifier.of(identifier("crushing"))
        val TRIAL_CATEGORY: CategoryIdentifier<TrialRecipeDisplay> = CategoryIdentifier.of(identifier("trial"))
        val LOOT_FABRICATOR_CATEGORY: CategoryIdentifier<LootFabricatorRecipeDisplay> = CategoryIdentifier.of(identifier("loot_fabricator"))

    }


    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(CrushingDisplayCategory())
        //registry.addWorkstations(CRUSHING_CATEGORY, EntryStacks.of(ITEM_SOOT_REDSTONE))

        registry.add(TrialDisplayCategory())
        registry.addWorkstations(TRIAL_CATEGORY, EntryStacks.of(BLOCK_TRIAL_KEYSTONE))

        registry.add(LootFabricatorDisplayCategory())
        registry.addWorkstations(LOOT_FABRICATOR_CATEGORY, EntryStacks.of(BLOCK_LOOT_FABRICATOR))
    }

    override fun registerDisplays(registry: DisplayRegistry){
        registry.registerFiller(CrushingRecipe::class.java) {
            CrushingRecipeDisplay(it)
        }

        registry.registerFiller(TrialKeystoneRecipe::class.java){
            TrialRecipeDisplay(it)
        }

        registry.registerFiller(LootFabricatorRecipe::class.java) {
            LootFabricatorRecipeDisplay(it)
        }
    }

    override fun postStage(manager: PluginManager<REIClientPlugin>?, stage: ReloadStage?) {
        val hiddenItems = listOf(ITEM_EMERITUS_HAT, ITEM_DML)
        EntryRegistry.getInstance().removeEntryIf {
            it.itemStack().item in hiddenItems
        }

        // Glitch Upgrade Smithing Template is only used for Glitch Armor - hide if not present
        EntryRegistry.getInstance().removeEntryIf {
            it.itemStack().isOf(ITEM_GLITCH_UPGRADE_SMITHING_TEMPLATE) && !isModLoaded(MODULAR_ARMOR_ID)
        }
    }

}