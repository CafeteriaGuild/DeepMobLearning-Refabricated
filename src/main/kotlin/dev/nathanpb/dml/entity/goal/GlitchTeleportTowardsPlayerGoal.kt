/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.entity.goal

import dev.nathanpb.dml.entity.SystemGlitchEntity
import dev.nathanpb.dml.trial.TrialGriefPrevention
import dev.nathanpb.dml.utils.randomOrNull
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.goal.FollowTargetGoal
import net.minecraft.entity.player.PlayerEntity
import kotlin.random.Random

class GlitchTeleportTowardsPlayerGoal(private val glitch: SystemGlitchEntity) : FollowTargetGoal<PlayerEntity?>(
    glitch,
    PlayerEntity::class.java as Class<PlayerEntity?>,
    false
) {

    private var ticksToTeleportCountdown = 0

    override fun canStart(): Boolean {
        glitch.trial.let { trial ->
            targetEntity = trial?.players?.filter {
                TrialGriefPrevention.isInArea(trial.pos, it.blockPos)
            }?.randomOrNull() ?: glitch.world.getClosestPlayer(TargetPredicate.DEFAULT, glitch)
        }

        return targetEntity != null
    }

    override fun stop() {
        targetEntity = null
        ticksToTeleportCountdown = 0
        super.stop()
    }

    override fun tick() {
        if (ticksToTeleportCountdown > 0) {
            ticksToTeleportCountdown--
        }
        if (targetEntity != null && ticksToTeleportCountdown <= 0 && Random.nextFloat() <= 0.05) {
            if (targetEntity.squaredDistanceTo(glitch) >= 25) {
                if (glitch.tryTeleportRandomly(targetEntity.blockPos, 2)) {
                    ticksToTeleportCountdown = 20 * 5
                    glitch.target = targetEntity
                    return
                }
            }
        }
    }
}
