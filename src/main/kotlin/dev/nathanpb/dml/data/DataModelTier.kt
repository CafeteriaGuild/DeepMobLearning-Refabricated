/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.data

import net.minecraft.text.TranslatableText
import kotlin.math.max

private val defaultWave = listOf(1, 1, 2, 3, 4, 4, 5, 5)

// TODO remove the hardcoded dataAmount
enum class DataModelTier(textEntry: String, val dataAmount: Int, val defaultWave: List<Int>) {
    FAULTY("tier.deepmoblearning.faulty", 0, defaultWave),
    BASIC("tier.deepmoblearning.basic", 8, defaultWave.map { it * 2 }),
    ADVANCED("tier.deepmoblearning.advanced", 16, defaultWave.map { it * 3 }),
    SUPERIOR("tier.deepmoblearning.superior", 32, defaultWave.map { it * 4 }),
    SELF_AWARE("tier.deepmoblearning.self_aware", 64, defaultWave.map { it * 5 });

    companion object {
        fun fromDataAmount(amount: Int) = values().last {
            it.dataAmount <= max(amount, 0)
        }

        fun fromIndex(index: Int): DataModelTier? {
            return if (index in (0.until(values().size))) {
                values()[index]
            } else null
        }
    }

    val text = TranslatableText(textEntry)
    fun isMaxTier() = this == values().last()
    fun nextTierOrCurrent() = if (isMaxTier()) SELF_AWARE else values()[ordinal+1]
}
