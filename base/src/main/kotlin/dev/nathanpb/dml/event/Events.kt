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

package dev.nathanpb.dml.event

import dev.nathanpb.dml.trial.Trial
import dev.nathanpb.dml.trial.TrialEndReason
import net.minecraft.entity.LivingEntity

val TrialStateChanged = event<(Trial)->Unit> { listeners ->
    { trial -> listeners.forEach { it(trial) } }
}

val TrialEndEvent = event<(Trial, reason: TrialEndReason)->Unit> { listeners ->
    { trial, reason ->
        listeners.forEach {
            it(trial, reason)
        }
    }
}

val TrialWaveSpawnEvent = event<(Trial, entities: List<LivingEntity>)->Unit> { listeners ->
    { trial, entities ->
        listeners.forEach {
            it(trial, entities)
        }
    }
}
