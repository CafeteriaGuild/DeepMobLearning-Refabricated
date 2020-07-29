package dev.nathanpb.dml

import dev.nathanpb.dml.block.registerBlocks
import dev.nathanpb.dml.blockEntity.registerBlockEntityTypes
import dev.nathanpb.dml.config.ModConfig
import dev.nathanpb.dml.config.registerConfigs
import dev.nathanpb.dml.entity.registerEntityRenderer
import dev.nathanpb.dml.entity.registerEntityTypes
import dev.nathanpb.dml.event.EndermanTeleportCallback
import dev.nathanpb.dml.event.LivingEntityDieCallback
import dev.nathanpb.dml.event.WorldExplosionCallback
import dev.nathanpb.dml.gui.registerGuis
import dev.nathanpb.dml.item.registerItems
import dev.nathanpb.dml.listener.CrushingRecipeListener
import dev.nathanpb.dml.listener.DataCollectListener
import dev.nathanpb.dml.net.registerClientSidePackets
import dev.nathanpb.dml.recipe.registerRecipeSerializers
import dev.nathanpb.dml.recipe.registerRecipeTypes
import dev.nathanpb.dml.screen.handler.registerContainerTypes
import dev.nathanpb.dml.screen.handler.registerScreenHandlers
import dev.nathanpb.dml.screen.registerScreens
import dev.nathanpb.dml.trial.TrialGriefPrevention
import dev.nathanpb.dml.trial.affix.core.TrialAffixRegistry
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.util.Identifier

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

lateinit var config: ModConfig
const val MOD_ID = "dml-refabricated"

@Suppress("unused")
fun init() {
    registerConfigs()
    registerItems()
    registerBlocks()
    registerBlockEntityTypes()
    registerContainerTypes()
    registerRecipeSerializers()
    registerRecipeTypes()
    registerClientSidePackets()
    registerScreenHandlers()
    registerEntityTypes()
    LivingEntityDieCallback.EVENT.register(DataCollectListener())
    AttackBlockCallback.EVENT.register(CrushingRecipeListener())
    TrialGriefPrevention().apply {
        AttackBlockCallback.EVENT.register(this)
        UseBlockCallback.EVENT.register(this)
        WorldExplosionCallback.EVENT.register(this)
        EndermanTeleportCallback.EVENT.register(this)
    }
    TrialAffixRegistry.registerDefaultAffixes()
    println("Deep Mob Learning: Refabricated is good to go")
}

@Suppress("unused")
fun initClient() {
    registerGuis()
    registerScreens()
    registerEntityRenderer()
}

fun identifier(path: String) = Identifier(MOD_ID, path)
