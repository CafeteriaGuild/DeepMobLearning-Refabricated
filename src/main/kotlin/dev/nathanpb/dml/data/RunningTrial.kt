/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.data

import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnType
import net.minecraft.util.math.BlockPos
import kotlin.random.Random

data class RunningTrialData (
    val recipe: TrialKeystoneRecipe,
    val waves: List<RunningTrialWaveData>
) {

    constructor(recipe: TrialKeystoneRecipe, blockEntity: BlockEntityTrialKeystone) : this(
        recipe, recipe.waves.mapIndexed { index, mobCount ->
            RunningTrialWaveData(index, mobCount, recipe.entity)
        }
    )
}

data class RunningTrialWaveData (
    val wave: Int,
    val entityCount: Int,
    val entityType: EntityType<*>
) {
    private val spawnedEntities = mutableListOf<LivingEntity>()
    var isSpawned = false
    fun isFinished() = isSpawned && !spawnedEntities.any(LivingEntity::isAlive)

    fun despawnWave() = spawnedEntities.forEach(LivingEntity::remove)

    fun spawnWave(blockEntity: BlockEntityTrialKeystone) {
        isSpawned = true
        (1..entityCount).forEach { _ ->
            entityType.spawn(
                blockEntity.world,
                null, null, null,
                BlockPos(
                    blockEntity.pos.x + Random.nextInt(-2, 2),
                    blockEntity.pos.y + 1,
                    blockEntity.pos.z + Random.nextInt(-2, 2)
                ),
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
