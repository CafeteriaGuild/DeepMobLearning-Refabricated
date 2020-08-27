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

import dev.nathanpb.dml.armor.modular.core.ModularEffectRegistry
import dev.nathanpb.dml.block.registerBlocks
import dev.nathanpb.dml.blockEntity.registerBlockEntityTypes
import dev.nathanpb.dml.config.ModConfig
import dev.nathanpb.dml.config.registerConfigs
import dev.nathanpb.dml.entity.SystemGlitchEntity
import dev.nathanpb.dml.entity.effect.registerStatusEffects
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
import dev.nathanpb.dml.net.registerServerSidePackets
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
    registerServerSidePackets()
    registerScreenHandlers()
    registerEntityTypes()
    registerStatusEffects()
    LivingEntityDieCallback.EVENT.register(DataCollectListener())
    AttackBlockCallback.EVENT.register(CrushingRecipeListener())
    TrialGriefPrevention().apply {
        AttackBlockCallback.EVENT.register(this)
        UseBlockCallback.EVENT.register(this)
        WorldExplosionCallback.EVENT.register(this)
        EndermanTeleportCallback.EVENT.register(this)
    }
    TrialAffixRegistry.registerDefaultAffixes()
    ModularEffectRegistry.registerDefaults()
    SystemGlitchEntity.registerDamageLimiter()
    println("Deep Mob Learning: Refabricated is good to go")
}

@Suppress("unused")
fun initClient() {
    registerGuis()
    registerScreens()
    registerEntityRenderer()
    registerKeybindings()
}

fun identifier(path: String) = Identifier(MOD_ID, path)
