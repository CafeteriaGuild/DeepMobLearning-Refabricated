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

package dev.nathanpb.dml.enums

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.config
import dev.nathanpb.dml.utils.takeOrNull
import net.minecraft.text.Text
import kotlin.math.ceil
import kotlin.math.max

enum class DataModelTier(textEntry: String, private val dataAmountSupplier: ()->Int, val glitchUpgradeOdds: Double) {
    FAULTY("tier.${MOD_ID}.faulty", { 0 }, config.trial.faultyGlitchUpgradeOdds),
    BASIC("tier.${MOD_ID}.basic", config.dataModel::basicDataRequired, config.trial.basicGlitchUpgradeOdds),
    ADVANCED("tier.${MOD_ID}.advanced", config.dataModel::advancedDataRequired, config.trial.advancedGlitchUpgradeOdds),
    SUPERIOR("tier.${MOD_ID}.superior", config.dataModel::superiorDataRequired, config.trial.superiorGlitchUpgradeOdds),
    SELF_AWARE("tier.${MOD_ID}.self_aware", config.dataModel::selfAwareDataRequired, config.trial.selfAwareGlitchUpgradeOdds);

    val dataAmount: Int
        get() = dataAmountSupplier()

    companion object {
        fun fromDataAmount(amount: Int) = values().last {
            it.dataAmount <= max(amount, 0)
        }

        fun fromIndex(index: Int): DataModelTier? {
            return takeOrNull(index in (0.until(values().size))) {
                values()[index]
            }
        }
    }

    val text = Text.translatable(textEntry)
    val glitchUpgradeOddsText = Text.literal("${(glitchUpgradeOdds * 100).toInt()}%")
    fun isMaxTier() = this == values().last()
    fun nextTierOrCurrent() = if (isMaxTier()) SELF_AWARE else values()[ordinal+1]
    val defaultWaveEntityCount = ceil((ordinal + 1) * 1.5).toInt()
    val systemGlitchMaxHealth = (ordinal + 1) * 100F
    val defaultWaveRespawnTimeout by lazy {
        (values().size - ordinal) * 5 * 20
    }
}