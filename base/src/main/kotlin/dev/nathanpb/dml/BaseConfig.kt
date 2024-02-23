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

class BaseConfig {

    var dataModel = DataModel()
    var trial = Trial()
    var machines = Machines()
    var misc = Misc()

}

/** Data Model */
class DataModel {
    var basicDataRequired = 8
    var advancedDataRequired = 16
    var superiorDataRequired = 32
    var selfAwareDataRequired = 64
    var hasSimulatedDataRestrictions = false

    var dataCollection = DataCollection()
}

class DataCollection {
    var dataGainPerKill = 1
    var glitchSwordDataGainPerKill = 2
}

/** Trial */
class Trial {
    var systemGlitch = SystemGlitch()

    var maxMobsInArena = 8
    var arenaRadius = 12
    var warmupTime = 60
    var maxTime = 24000

    var allowStartInWrongTerrain = false
    var allowPlayersLeavingArena = false
    var allowMobsLeavingArena = false
    var buildGriefPrevention = true
    var interactGriefPrevention = true
    var explosionGriefPrevention = true

    var trialKeyConsume = true
    var trialKeyReturnIfSucceed = true

    var faultyGlitchUpgradeOdds = .0
    var basicGlitchUpgradeOdds = .0
    var advancedGlitchUpgradeOdds = .15
    var superiorGlitchUpgradeOdds = .65
    var selfAwareGlitchUpgradeOdds = 1.0

    var affixes = TrialAffix()
}

class TrialAffix {
    var maxAffixesInKey = 3
    var enableMobStrength = true
    var enableMobSpeed = true
    var enableMobResistance = true
    var enableThunderstorm = true
    var enablePartyPoison = true

    var thunderstormBoltChance = .05F
    var partyPoisonChance = .005F
}

class SystemGlitch {
    var teleportChance = 0.05F
    var teleportMinDistance = 5
    var teleportDelay = 100
    var teleportAroundPlayerRadius = 2

    var damageLimiter = 20F
}

/** Machines */
class Machines {

    var dataSynthesizer = DataSynthesizer()
    var lootFabricator = LootFabricator()

}

class DataSynthesizer { // FIXME needs rebalancing
    var energyCapacity = 196608L
    var energyIO = 8192L
}

class LootFabricator {
    var overworldExchangeRatio = 16
    var zombieExchangeRatio = 16
    var skeletonExchangeRatio = 16
    var slimyExchangeRatio = 16
    var illagerExchangeRatio = 13
    var oceanExchangeRatio = 16
    var ghostExchangeRatio = 16
    var netherExchangeRatio = 10
    var endExchangeRatio = 14
    var unstackableNullificationChance = .75F

    var processTime = 200
    var energyCapacity = 16384L
    var energyInput = 8192L
}


/** Misc */
class Misc {

    var glitchSword = GlitchSword()
    var energyOctahedron = EnergyOctahedron()
}

class GlitchSword { // FIXME balance accordingly to Data Synthesizer

    var energyCapacity = 12500L
    var energyInput = 2048L
    var usageCost = 200L

}

class EnergyOctahedron { // FIXME balance accordingly to Data Synthesizer

    var energyCapacity = 32768L
    var energyIO = 4096L

}