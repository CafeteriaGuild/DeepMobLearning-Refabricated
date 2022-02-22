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
import dev.nathanpb.dml.command.DMLCommand
import dev.nathanpb.dml.entity.registerEntityRenderer
import dev.nathanpb.dml.entity.registerEntityTypes
import dev.nathanpb.dml.event.VanillaEvents
import dev.nathanpb.dml.item.ITEM_DML
import dev.nathanpb.dml.item.registerItems
import dev.nathanpb.dml.itemgroup.registerItemGroup
import dev.nathanpb.dml.listener.CrushingRecipeListener
import dev.nathanpb.dml.listener.DataCollectListener
import dev.nathanpb.dml.recipe.registerRecipeSerializers
import dev.nathanpb.dml.recipe.registerRecipeTypes
import dev.nathanpb.dml.screen.handler.registerScreenHandlers
import dev.nathanpb.dml.screen.registerScreens
import dev.nathanpb.dml.trial.TrialGriefPrevention
import dev.nathanpb.dml.trial.affix.core.TrialAffixRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import kotlin.random.Random

const val MOD_ID = "dml-refabricated"

val LOGGER = LoggerFactory.getLogger(MOD_ID)

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
    CommandRegistrationCallback.EVENT.register(DMLCommand())
    EntityCategoryRegistry.INSTANCE
    LOGGER.info("Deep Mob Learning: Refabricated" + quirkyStartupMessages[Random.nextInt(quirkyStartupMessages.size)])
}

@Suppress("unused")
fun initClient() {
    registerScreens()
    registerEntityRenderer()
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