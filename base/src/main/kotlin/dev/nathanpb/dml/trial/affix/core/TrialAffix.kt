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

package dev.nathanpb.dml.trial.affix.core

import dev.nathanpb.dml.trial.Trial
import net.minecraft.entity.LivingEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier


abstract class TrialAffix(val id: Identifier) {
    val name = Text.translatable("affix.${id.namespace}.${id.path}.name")

    abstract fun isEnabled(): Boolean

    private fun shouldBeInvoked(trial: Trial): Boolean {
        return isEnabled() && this in trial.affixes
    }

    fun attemptToInvoke(trial: Trial, runner: ()->Unit) {
        if (shouldBeInvoked(trial)) {
            runner()
        }
    }

    interface WaveSpawnedListener {
        fun onWaveSpawn(trial: Trial, waveEntities: List<LivingEntity>)
    }

    interface TickableAffix {
        fun tick(trial: Trial)
    }
}
