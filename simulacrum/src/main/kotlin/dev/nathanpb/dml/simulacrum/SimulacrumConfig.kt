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

package dev.nathanpb.dml.simulacrum

class SimulacrumConfig {

    var simulationChamber = SimulationChamber()
    var matterXP = MatterXP()
}

class SimulationChamber {
    var basicTierPristineChance = 5
    var advancedTierPristineChance = 11
    var superiorTierPristineChance = 24
    var selfAwareTierPristineChance = 42

    var energyCostMultiplier = 0.25F // divide model's energy value by 4

    var dataBonus = 1
}

class MatterXP {
    var overworldMatterXP = 10
    var hellishMatterXP = 14
    var extraterrestrialMatterXP = 20
}