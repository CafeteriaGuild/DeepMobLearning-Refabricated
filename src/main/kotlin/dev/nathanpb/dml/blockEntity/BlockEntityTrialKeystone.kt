/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.TrialKeystoneAlreadyRunningException
import dev.nathanpb.dml.TrialKeystoneWrongTerrainException
import dev.nathanpb.dml.data.RunningTrialData
import dev.nathanpb.dml.enum.TrialEndReason
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class BlockEntityTrialKeystone :
    BlockEntity(BLOCKENTITY_TRIAL_KEYSTONE),
    Tickable
{

    companion object {
        const val EFFECTIVE_AREA_RADIUS = 12.0
        const val EFFECTIVE_AREA_RADIUS_SQUARED = EFFECTIVE_AREA_RADIUS * EFFECTIVE_AREA_RADIUS
    }

    private var circleBounds: List<BlockPos>? = null
    private var currentTrial: RunningTrialData? = null
    private var currentWave = 0
    private var tickCount = 0

    override fun tick() {
        if (world?.isClient == true) {
            val vec3d = Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            MinecraftClient.getInstance().player?.let { clientPlayer ->
                if (clientPlayer.squaredDistanceTo(vec3d) <= EFFECTIVE_AREA_RADIUS_SQUARED) {
                    checkTerrain().let { wrongTerrain ->
                        if (wrongTerrain.isNotEmpty()) {
                            (0 .. min(wrongTerrain.size / 4, 1)).let { _ ->
                                wrongTerrain.random().let {
                                    world?.addParticle(
                                        ParticleTypes.FLAME,
                                        true,
                                        it.x + Random.nextDouble() - .1,
                                        it.y + 1.0,
                                        it.z + Random.nextDouble() - .1,
                                        0.0, 0.0, 0.0
                                    )
                                }
                            }
                        }
                    }
                }
            }
            return
        }
        currentTrial?.let { currentTrial ->
            if (getPlayersAround().isEmpty()) {
                endTrial(TrialEndReason.NO_ONE_IS_AROUND)
                return
            }
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
            } else endTrial(TrialEndReason.SUCCESS)
            tickCount++
        }
    }

    @Suppress("private")
    fun isRunning() = currentTrial != null

    @Suppress("private")
    fun startTrial(recipe: TrialKeystoneRecipe) {
        if (!isRunning()) {
            checkTerrain().let { wrongTerrain ->
                if (wrongTerrain.isEmpty()) {
                    currentTrial = RunningTrialData(recipe, this)
                } else throw TrialKeystoneWrongTerrainException(this, wrongTerrain)
            }
        } else throw TrialKeystoneAlreadyRunningException(this)
    }

    @Suppress("private")
    fun endTrial(reason: TrialEndReason) {
        if (isRunning()) {
            when (reason) {
                TrialEndReason.SUCCESS -> dropRewards()
                TrialEndReason.NO_ONE_IS_AROUND -> currentTrial?.waves?.get(currentWave)?.despawnWave()
            }
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

    override fun setLocation(world: World, pos: BlockPos) {
        super.setLocation(world, pos)
        circleBounds = getCircleBoundBlocks()
    }

    private fun getPlayersAround() : List<PlayerEntity> {
        return world?.getEntities(EntityType.PLAYER, Box(
            pos.x - EFFECTIVE_AREA_RADIUS,
            pos.y - 1.0,
            pos.z - EFFECTIVE_AREA_RADIUS,
            pos.x + EFFECTIVE_AREA_RADIUS,
            pos.y + EFFECTIVE_AREA_RADIUS,
            pos.z + EFFECTIVE_AREA_RADIUS
        )) {
            it.squaredDistanceTo(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()) <= EFFECTIVE_AREA_RADIUS_SQUARED
        } ?: emptyList()
    }

    /**
     * Checks the effective terrain and returns a list of wrong blocks
     *
     * @return the list of erroneous blocks
     */
    private fun checkTerrain() = mutableListOf<BlockPos>().also { list ->
        val radInt = EFFECTIVE_AREA_RADIUS.toInt()
        (pos.x - radInt .. pos.x + radInt).forEach { x ->
            (pos.z - radInt .. pos.z + radInt).forEach { z ->

                // Searching for non-solid blocks bellow the circle
                val floorPos = BlockPos(x, pos.y - 1, z)
                if (floorPos.getSquaredDistance(pos) <= EFFECTIVE_AREA_RADIUS_SQUARED) {
                    world?.getBlockState(floorPos)?.let { floorBlock ->
                        if (!floorBlock.isSideSolidFullSquare(world, floorPos, Direction.UP)) {
                            list += floorPos
                        }
                    }
                }

                // Searching for non-air blocks inside the dome
                (pos.y .. pos.y + radInt).forEach { y ->
                    val innerBlockPos = BlockPos(x, y, z)
                    if (
                        innerBlockPos != pos
                        && innerBlockPos.getSquaredDistance(pos) <= EFFECTIVE_AREA_RADIUS_SQUARED
                        && world?.getBlockState(innerBlockPos)?.isAir != true
                    ) {
                        list += innerBlockPos
                    }
                }
            }
        }
    }.toList()

    private fun getCircleBoundBlocks() = mutableListOf<BlockPos>().also { list ->
        (1..360).forEach { angle ->
            BlockPos(
                pos.x + (EFFECTIVE_AREA_RADIUS * cos(angle * Math.PI / 180)),
                pos.y.toDouble(),
                pos.z + (EFFECTIVE_AREA_RADIUS * sin(angle * Math.PI / 180))
            ).let {
                if (it !in list) {
                    list += it
                }
            }
        }
    }.toList()
}
