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

package dev.nathanpb.dml

import dev.nathanpb.dml.block.registerBlocks
import dev.nathanpb.dml.blockEntity.registerBlockEntityTypes
import dev.nathanpb.dml.command.DMLCommand
import dev.nathanpb.dml.entity.registerEntityTypes
import dev.nathanpb.dml.event.VanillaEvents
import dev.nathanpb.dml.item.registerItems
import dev.nathanpb.dml.itemgroup.registerItemGroup
import dev.nathanpb.dml.listener.CrushingRecipeListener
import dev.nathanpb.dml.listener.DataCollectListener
import dev.nathanpb.dml.misc.lootfunction.registerLootFunctions
import dev.nathanpb.dml.misc.registerSounds
import dev.nathanpb.dml.recipe.registerRecipeSerializers
import dev.nathanpb.dml.recipe.registerRecipeTypes
import dev.nathanpb.dml.screen.handler.registerScreenHandlers
import dev.nathanpb.dml.trial.TrialGriefPrevention
import dev.nathanpb.dml.trial.affix.core.TrialAffixRegistry
import dev.nathanpb.dml.utils.initConfig
import dev.nathanpb.dml.worldgen.registerFeatures
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random

const val MOD_ID = "dml-refabricated"

val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

val baseConfig: BaseConfig = initConfig("base", BaseConfig(), BaseConfig::class.java)

@Suppress("unused")
fun init() {
    registerItems()
    registerBlocks()
    registerItemGroup()
    registerBlockEntityTypes()
    registerRecipeSerializers()
    registerRecipeTypes()
    registerScreenHandlers()
    registerEntityTypes()
    ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(DataCollectListener())
    AttackBlockCallback.EVENT.register(CrushingRecipeListener())
    TrialGriefPrevention().apply {
        AttackBlockCallback.EVENT.register(this)
        UseBlockCallback.EVENT.register(this)
        VanillaEvents.WorldExplosionEvent.register(this::explode)
        VanillaEvents.EndermanTeleportEvent.register(this::onEndermanTeleport)
    }
    TrialAffixRegistry.registerDefaultAffixes()
    registerFeatures()
    registerLootFunctions()
    registerSounds()
    CommandRegistrationCallback.EVENT.register(DMLCommand())
    LOGGER.info("Deep Mob Learning: Refabricated" + quirkyStartupMessages[Random.nextInt(quirkyStartupMessages.size)])
}

fun identifier(path: String) = Identifier(MOD_ID, path)

val quirkyStartupMessages = arrayOf(
    " is good to go",
    "'s body is ready!",
    " is starting up in... well, that depends on the other mods, really.",
    " will be challenging the System Glitch soon!",
    " had a good 8 hour sleep and is ready for the day.",
    " is warming up!"
)