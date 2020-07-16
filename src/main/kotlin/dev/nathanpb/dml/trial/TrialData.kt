/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.trial

import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import net.minecraft.entity.EntityType

data class TrialData (
    val recipe: TrialKeystoneRecipe,
    val waves: List<TrialWaveData>
)

data class TrialWaveData (
    val wave: Int,
    val entityCount: Int,
    val entityType: EntityType<*>
) {
    companion object {
        fun fromRecipe(recipe: TrialKeystoneRecipe) = recipe.waves.mapIndexed { index, mobCount ->
            TrialWaveData(index, mobCount, recipe.entity)
        }
    }
}
