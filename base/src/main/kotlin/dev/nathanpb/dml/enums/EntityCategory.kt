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
import dev.nathanpb.dml.baseConfig
import dev.nathanpb.dml.item.*
import dev.nathanpb.dml.utils.initTag
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey
import net.minecraft.text.Text


enum class EntityCategory(
    val tagKey: TagKey<EntityType<*>>,
    val energyValue: Int,
    val exchangeRatio: Int,
    val matterType: MatterType, /** simulacrum only! */
    private val itemForRendering: ()->Item = ::ITEM_DATA_MODEL // TODO show category stuff on REI?
) {

    OVERWORLD(
        initTag("overworld_mobs"),
        baseConfig.machines.dataSynthesizer.overworldDataEnergyValue,
        baseConfig.machines.lootFabricator.overworldExchangeRatio,
        MatterType.OVERWORLD,
        ::ITEM_DATA_MODEL_OVERWORLD
    ),
    ZOMBIE(
        initTag("zombie_mobs"),
        baseConfig.machines.dataSynthesizer.zombieDataEnergyValue,
        baseConfig.machines.lootFabricator.zombieExchangeRatio,
        MatterType.OVERWORLD,
        ::ITEM_DATA_MODEL_ZOMBIE
    ),
    SKELETON(
        initTag("skeleton_mobs"),
        baseConfig.machines.dataSynthesizer.skeletonDataEnergyValue,
        baseConfig.machines.lootFabricator.skeletonExchangeRatio,
        MatterType.OVERWORLD,
        ::ITEM_DATA_MODEL_SKELETON
    ),
    SLIMY(
        initTag("slimy_mobs"),
        baseConfig.machines.dataSynthesizer.slimyDataEnergyValue,
        baseConfig.machines.lootFabricator.slimyExchangeRatio,
        MatterType.OVERWORLD,
        ::ITEM_DATA_MODEL_SLIMY
    ),
    ILLAGER(
        initTag("illager_mobs"),
        baseConfig.machines.dataSynthesizer.illagerDataEnergyValue,
        baseConfig.machines.lootFabricator.illagerExchangeRatio,
        MatterType.OVERWORLD,
        ::ITEM_DATA_MODEL_ILLAGER
    ),
    OCEAN(
        initTag("ocean_mobs"),
        baseConfig.machines.dataSynthesizer.oceanDataEnergyValue,
        baseConfig.machines.lootFabricator.oceanExchangeRatio,
        MatterType.OVERWORLD,
        ::ITEM_DATA_MODEL_OCEAN
    ),
    GHOST(
        initTag("ghost_mobs"),
        baseConfig.machines.dataSynthesizer.ghostDataEnergyValue,
        baseConfig.machines.lootFabricator.ghostExchangeRatio,
        MatterType.OVERWORLD,
        ::ITEM_DATA_MODEL_GHOST
    ),
    NETHER(
        initTag("nether_mobs"),
        baseConfig.machines.dataSynthesizer.netherDataEnergyValue,
        baseConfig.machines.lootFabricator.netherExchangeRatio,
        MatterType.HELLISH,
        ::ITEM_DATA_MODEL_NETHER
    ),
    END(
        initTag("end_mobs"),
        baseConfig.machines.dataSynthesizer.endDataEnergyValue,
        baseConfig.machines.lootFabricator.endExchangeRatio,
        MatterType.EXTRATERRESTRIAL,
        ::ITEM_DATA_MODEL_END
    );


    val displayName: Text = Text.translatable("mobcategory.${MOD_ID}.${tagKey.id.path}")



}
