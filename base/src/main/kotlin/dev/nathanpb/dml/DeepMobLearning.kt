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

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import dev.nathanpb.dml.block.registerBlocks
import dev.nathanpb.dml.blockEntity.registerBlockEntityTypes
import dev.nathanpb.dml.entity.registerEntityRenderer
import dev.nathanpb.dml.entity.registerEntityTypes
import dev.nathanpb.dml.event.EndermanTeleportEvent
import dev.nathanpb.dml.event.WorldExplosionEvent
import dev.nathanpb.dml.item.registerItems
import dev.nathanpb.dml.listener.CrushingRecipeListener
import dev.nathanpb.dml.listener.DataCollectListener
import dev.nathanpb.dml.recipe.registerRecipeSerializers
import dev.nathanpb.dml.recipe.registerRecipeTypes
import dev.nathanpb.dml.screen.handler.registerScreenHandlers
import dev.nathanpb.dml.screen.registerScreens
import dev.nathanpb.dml.trial.TrialGriefPrevention
import dev.nathanpb.dml.trial.affix.core.TrialAffixRegistry
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files

const val MOD_ID = "dml-refabricated"
val LOGGER = LogManager.getLogger("Deep Mob Learning: Refabricated")

val config: ModConfig by lazy {
    val parser = JsonParser()
    val gson = GsonBuilder().setPrettyPrinting().create()
    val configFile = File("${FabricLoader.getInstance().configDir}${File.separator}$MOD_ID.json")
    var finalConfig: ModConfig
    LOGGER.info("Trying to read config file...")
    try {
        if (configFile.createNewFile()) {
            LOGGER.info("No config file found, creating a new one...")
            val json: String = gson.toJson(parser.parse(gson.toJson(ModConfig())))
            PrintWriter(configFile).use { out -> out.println(json) }
            finalConfig = ModConfig()
            LOGGER.info("Successfully created default config file.")
        } else {
            LOGGER.info("A config file was found, loading it..")
            finalConfig = gson.fromJson(String(Files.readAllBytes(configFile.toPath())), ModConfig::class.java)
            if (finalConfig == null) {
                throw NullPointerException("The config file was empty.")
            } else {
                LOGGER.info("Successfully loaded config file.")
            }
        }
    } catch (exception: Exception) {
        LOGGER.error("There was an error creating/loading the config file!", exception)
        finalConfig = ModConfig()
        LOGGER.warn("Defaulting to original config.")
    }
    finalConfig
}

@Suppress("unused")
fun init() {
    registerItems()
    registerBlocks()
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
        WorldExplosionEvent.register(this::explode)
        EndermanTeleportEvent.register(this::onEndermanTeleport)
    }
    TrialAffixRegistry.registerDefaultAffixes()
    LOGGER.info("Deep Mob Learning: Refabricated is good to go")
}

@Suppress("unused")
fun initClient() {
    registerScreens()
    registerEntityRenderer()
}

fun identifier(path: String) = Identifier(MOD_ID, path)
