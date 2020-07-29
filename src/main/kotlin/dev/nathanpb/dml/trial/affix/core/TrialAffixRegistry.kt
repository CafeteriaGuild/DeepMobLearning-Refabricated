/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.trial.affix.core

import dev.nathanpb.dml.event.TrialWaveSpawnCallback
import dev.nathanpb.dml.trial.affix.MobResistanceTrialAffix
import dev.nathanpb.dml.trial.affix.MobSpeedTrialAffix
import dev.nathanpb.dml.trial.affix.MobStrengthTrialAffix
import dev.nathanpb.dml.utils.randomOrNull
import net.minecraft.util.Identifier
import org.jetbrains.annotations.ApiStatus

class TrialAffixRegistry private constructor() {
    companion object {
        @Suppress("private")
        val INSTANCE = TrialAffixRegistry()

        @ApiStatus.Internal
        fun registerDefaultAffixes() {
            INSTANCE.register(MobStrengthTrialAffix())
            INSTANCE.register(MobSpeedTrialAffix())
            INSTANCE.register(MobResistanceTrialAffix())
        }
    }

    private val registry = mutableListOf<TrialAffix>()

    @Suppress("private", "unused")
    val all: List<TrialAffix>
        get() = registry.toList()

    @Suppress("private")
    fun register(affix: TrialAffix) {
        if (!isRegistered(affix.id)) {
            registry += affix
            if (affix is TrialAffix.WaveSpawnedListener) {
                TrialWaveSpawnCallback.EVENT.register(TrialWaveSpawnCallback { trial, waveEntities ->
                    affix.attemptToInvoke(trial) {
                        affix.onWaveSpawn(trial, waveEntities)
                    }
                })
            }
        } else {
            throw DuplicatedRegistryException(affix.id)
        }
    }

    @Suppress("private")
    fun isRegistered(id: Identifier) = registry.any {
        it.id == id
    }

    @Suppress("private", "unused")
    fun findById(id: Identifier) = registry.firstOrNull {
        it.id == id
    }

    fun pickRandomEnabled() = registry.filter(TrialAffix::isEnabled).randomOrNull()
}
