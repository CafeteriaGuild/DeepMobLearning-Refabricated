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

}



@Config(name = "trial")
class Trial : ConfigData {
    var maxMobsInArena = 32
    var postEndTimeout = 60
    var arenaRadius = 12

    var allowStartInWrongTerrain = false
}
