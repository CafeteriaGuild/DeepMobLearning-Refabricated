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

package dev.nathanpb.dml.trial.affix

import dev.nathanpb.dml.config
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.trial.Trial
import dev.nathanpb.dml.trial.TrialState
import dev.nathanpb.dml.trial.affix.core.TrialAffix
import dev.nathanpb.dml.utils.randomAround
import net.minecraft.entity.EntityType
import net.minecraft.util.math.Vec3d
import kotlin.random.Random

class ThunderstormAffix : TrialAffix(identifier("thunderstorm")), TrialAffix.TickableAffix {

    override fun isEnabled() = config.affix.enableThunderstorm

    override fun tick(trial: Trial) {
        if (trial.state == TrialState.RUNNING && Random.nextFloat() < config.affix.thunderstormBoltChance) {
            val pos = trial.pos.randomAround(config.trial.arenaRadius, 0, config.trial.arenaRadius)

            // code copied from ServerWorld
            EntityType.LIGHTNING_BOLT.create(trial.world)?.let { lightningEntity ->
                val bl2 = trial.world.random.nextDouble() < trial.world.getLocalDifficulty(pos).localDifficulty * 0.01
                lightningEntity.method_29495(Vec3d.ofBottomCenter(pos))
                lightningEntity.method_29498(bl2)
                trial.world.spawnEntity(lightningEntity)
            }
        }
    }
}
