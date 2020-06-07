/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.TrialKeystoneAlreadyRunningException
import dev.nathanpb.dml.data.RunningTrialData
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.util.Tickable

class BlockEntityTrialKeystone :
    BlockEntity(BLOCKENTITY_TRIAL_KEYSTONE),
    Tickable
{
    private var currentTrial: RunningTrialData? = null
    private var currentWave = 0
    private var tickCount = 0

    override fun tick() {
        if (world?.isClient == true) return
        currentTrial?.let { currentTrial ->
            if (currentWave < currentTrial.waves.size) {
                val wave = currentTrial.waves[currentWave]
                if (!wave.isSpawned) {
                    // Will spawn the current wave if its not spawned yet
                    // Applied when the last tick just moved to the next wave but did not spawned it yet
                    startCurrentWave()
                } else if (wave.isFinished()) {
                    // Will move to the next wave in the current tick
                    // The next tick will check if the wave exists and
                    // spawn the wave if so, or finish the trial if not
                    currentWave++
                }
            } else endTrial()
            tickCount++
        }
    }

    @Suppress("private")
    fun isRunning() = currentTrial != null

    @Suppress("private")
    fun startTrial(recipe: TrialKeystoneRecipe) {
        if (!isRunning()) {
            currentTrial = RunningTrialData(recipe, this)
        } else throw TrialKeystoneAlreadyRunningException(this)
    }

    @Suppress("private")
    fun endTrial() {
        if (isRunning()) {
            dropRewards()
            currentTrial = null
            currentWave = 0
            tickCount = 0
        } else throw TrialKeystoneAlreadyRunningException(this)
    }

    private fun dropRewards() {
        currentTrial?.recipe?.copyRewards()?.map {
            ItemEntity(world, pos.x.toDouble(), pos.y + 1.0, pos.z.toDouble(), it)
        }?.forEach {
            world?.spawnEntity(it)
        }
    }

    private fun startCurrentWave() {
        currentTrial?.waves?.get(currentWave)?.spawnWave(this)
    }
}
