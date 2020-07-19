/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.trial

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.random.Random

class TrialWave (
    val waveData: TrialWaveData
) {
    val spawnedEntities = mutableListOf<LivingEntity>()
    var isSpawned = false
    fun isFinished() = isSpawned && !spawnedEntities.any(LivingEntity::isAlive)

    fun despawnWave() = spawnedEntities.forEach(LivingEntity::remove)

    fun spawnWave(world: World, pos: BlockPos) {
        isSpawned = true
        (1..waveData.entityCount).forEach { _ ->
            waveData.category.tag.getRandom(java.util.Random()).spawn(
                world,
                null, null, null,
                pos.add(Random.nextInt(-2, 2), 5, Random.nextInt(-2, 2)),
                SpawnType.SPAWNER,
                false, false
            ).let {
                if (it is LivingEntity) {
                    spawnedEntities.add(it)
                }
            }
        }
    }
}
