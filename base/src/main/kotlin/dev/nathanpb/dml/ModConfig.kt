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

class ModConfig {

    var trial = Trial()
    var lootFabricator = LootFabricator()
    var dataModel = DataModel()
    var systemGlitch = SystemGlitch()
    var dataCollection = DataCollection()
    var affix = TrialAffix()
    var matterCondenser = MatterCondenser()
    var glitchArmor = GlitchArmor()
}

class Trial {
    var maxMobsInArena = 8
    var postEndTimeout = 60
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


class LootFabricator {
    var pristineExchangeRate = 16
    var processTime = 200
}


class DataModel {

    var basicDataRequired = 8
    var advancedDataRequired = 16
    var superiorDataRequired = 32
    var selfAwareDataRequired = 64
}


class SystemGlitch {

    var teleportChance = 0.05F
    var teleportMinDistance = 5
    var teleportDelay = 100
    var teleportAroundPlayerRadius = 2

    var damageLimiter = 20F
}


class DataCollection {
    var baseDataGainPerKill = 1
}


class MatterCondenser {
    var processTime = 40
}


class GlitchArmor {
    var dataAmountToBasic = 32
    var dataAmountToAdvanced = 96
    var dataAmountToSuperior = 192
    var dataAmountToSelfAware = 384
    var soulVisionRange = 12
    var maxFlightTicksPerLevel = 30 * 20
    var undyingCooldownTime = 36000

    val costs = GlitchArmorDataConsume()
}

class GlitchArmorDataConsume {
    var fireProtection = 1F
    var piglinTruce = 3.5F
    var autoExtinguish = 4F
    var featherFalling = 3F
    var fireImmunity = 1F
    var jumpBoost = .0075F
    var plenty = 1F
    var unrottenFlesh = .5F
    var rotResistance = .5F
    var zombieFriendly = .1F
    var archery = .5F
    var skeletonFriendly = .1F
    var fallImmunity = 1F
    var endermenProofVision = 1F
    var shulkerFriendly = .2F
    var teleports = 1.5F
    var soulVision = 8F
    var nightVision = .005F
    var fly = 0.05F
    var underwaterHaste = .01F
    var depthStrider = .01F
    var waterBreathing = .01F
    var poseidonBless = .015F
    var resistance = .3F
    var undying = 8F
}
