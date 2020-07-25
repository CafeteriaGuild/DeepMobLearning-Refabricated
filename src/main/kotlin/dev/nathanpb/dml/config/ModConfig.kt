/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.config

import dev.nathanpb.dml.config
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer


fun registerConfigs() {
    AutoConfig.register(ModConfig::class.java, ::GsonConfigSerializer)
    config = AutoConfig.getConfigHolder(ModConfig::class.java).config
}

@Config(name = "deepmoblearning")
class ModConfig : ConfigData {

    @ConfigEntry.Category("trial")
    @ConfigEntry.Gui.TransitiveObject
    var trial = Trial()

    @ConfigEntry.Category("loot_fabricator")
    @ConfigEntry.Gui.TransitiveObject
    var lootFabricator = LootFabricator()

    @ConfigEntry.Category("data_model")
    @ConfigEntry.Gui.TransitiveObject
    var dataModel = DataModel()

    override fun validatePostLoad() {
       dataModel.validatePostLoad()
    }

}

@Config(name = "trial")
class Trial : ConfigData {
    var maxMobsInArena = 32
    var postEndTimeout = 60
    var arenaRadius = 12

    var allowStartInWrongTerrain = false
    var allowPlayersLeavingArena = false
    var allowMobsLeavingArena = false
    var buildGriefPrevention = true
    var interactGriefPrevention = true
    var explosionGriefPrevention = true

    var trialKeyConsume = true
    var trialKeyReturnIfSucceed = true
}

@Config(name = "loot_fabricator")
class LootFabricator : ConfigData {
    var pristineExchangeRate = 16
    var processTime = 200
}

@Config(name = "data_model")
class DataModel : ConfigData {

    var basicDataRequired = 8
    var advancedDataRequired = 16
    var superiorDataRequired = 32
    var selfAwareDataRequired = 64

    override fun validatePostLoad() {
        if (basicDataRequired <= 0) {
            basicDataRequired = 1
        }
        if (advancedDataRequired <= basicDataRequired) {
            advancedDataRequired = basicDataRequired + 1
        }
        if (superiorDataRequired <= advancedDataRequired) {
            superiorDataRequired = basicDataRequired + 1
        }
        if (selfAwareDataRequired <= advancedDataRequired) {
            selfAwareDataRequired = advancedDataRequired + 1
        }
    }
}
