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

package dev.nathanpb.dml.modular_armor

class ModularArmorConfig {

    var machines = Machines()
    var glitchArmor = GlitchArmor()
    var misc = Misc()

}

/** Machines */
class Machines {

    var matterCondenser = MatterCondenser()

}

class MatterCondenser {

    var normalToPristineEnergyMultiplier = 8L // multiply model's energy value

    var energyCapacity = 262144L
    var energyIO = 8192L
}

/** Glitch Armor */
class GlitchArmor {

    var energyCapacityMultiplier = 125000L
    var energyIOMultiplier = 625L
    var allowEnergyOutput = false

    var soulVisionRange = 12
    var undyingCooldownTime = 36000

    val costs = GlitchArmorDataConsume()
}

class GlitchArmorDataConsume {
    var fireProtection = 16L
    var piglinTruce = 8L
    var autoExtinguish = 25L
    var featherFalling = 15L
    var fireImmunity = 32L
    var jumpBoost = 10L
    var plenty = 8L
    var rotResistance = 5L
    var unrottenFlesh = 20L
    var zombieFriendly = 5L
    var archery = 0L // FIXME
    var skeletonFriendly = 7L
    var fallImmunity = 30L
    var endermenProofVision = 10L
    var shulkerFriendly = 10L
    var teleports = 25L
    var soulVision = 16L
    var nightVision = 8L
    var fly = 20L
    var underwaterHaste = 10L
    var depthStrider = 15L
    var waterBreathing = 10L
    var poseidonBless = 30L
    var resistance = 20L
    var undying = 1000L
}

/** Misc */
class Misc {

    var pristineEnergyCube = PristineEnergyCube()
}


class PristineEnergyCube { // FIXME balance accordingly to Matter Condenser

    var energyCapacity = 32768L
    var energyIO = 4096L
}