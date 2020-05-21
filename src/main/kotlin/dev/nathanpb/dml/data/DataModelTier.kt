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
import kotlin.math.min

// TODO remove the hardcoded dataAmount
enum class DataModelTier(textEntry: String, val dataAmount: Int) {
    FAULTY("tier.deepmoblearning.faulty", 0),
    BASIC("tier.deepmoblearning.basic", 8),
    ADVANCED("tier.deepmoblearning.advanced", 16),
    SUPERIOR("tier.deepmoblearning.superior", 32),
    SELF_AWARE("tier.deepmoblearning.self_aware", 64);

    companion object {
        fun fromDataAmount(amount: Int) = values().last {
            it.dataAmount <= max(amount, 0)
        }
    }

    val text = TranslatableText(textEntry)
}
