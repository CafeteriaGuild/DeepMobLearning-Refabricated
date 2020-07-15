/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.TrialKeystoneAlreadyRunningException
import dev.nathanpb.dml.TrialKeystoneNoPlayersAround
import dev.nathanpb.dml.TrialKeystoneWrongTerrainException
import dev.nathanpb.dml.data.RunningTrialData
import dev.nathanpb.dml.data.TrialPlayerData
import dev.nathanpb.dml.enum.TrialEndReason
import dev.nathanpb.dml.net.TRIAL_ENDED_PACKET
import dev.nathanpb.dml.net.TRIAL_UPDATED_PACKET
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import dev.nathanpb.dml.utils.getEntitiesAroundCircle
import dev.nathanpb.dml.utils.toVec3d
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import kotlin.math.cos
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
        val RUNNING_TRIALS = mutableListOf<BlockEntityTrialKeystone>()
    }

    private var circleBounds: List<BlockPos>? = null
    private var currentTrial: RunningTrialData? = null
    private var players: List<PlayerEntity>? = null
    private var currentWave = 0
    private var tickCount = 0

    override fun tick() {
        if (world?.isClient == true) {
            MinecraftClient.getInstance().player?.let { clientPlayer ->
                if (clientPlayer.squaredDistanceTo(pos.toVec3d()) <= EFFECTIVE_AREA_RADIUS_SQUARED) {
                    spawnParticlesInWrongTerrain()
                }
            }
            return
        }
        currentTrial?.let { currentTrial ->
            if (!areIntegrantsAround()) {
                endTrial(TrialEndReason.NO_ONE_IS_AROUND)
                return
            }
            if (currentWave < currentTrial.waves.size) {
                pullMobsInBorders()
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
                    world?.getEntitiesAroundCircle(EntityType.PLAYER, pos, EFFECTIVE_AREA_RADIUS)?.let { playersAround ->
                        if (playersAround.isNotEmpty()) {
                            players = playersAround
                            currentTrial = RunningTrialData(recipe, this)
                            RUNNING_TRIALS += this
                        } else throw TrialKeystoneNoPlayersAround(this)
                    }
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
            RUNNING_TRIALS -= this
            sendTrialEndPackets(reason)
        } else throw TrialKeystoneAlreadyRunningException(this)
    }

    /**
     * Spawns fire particles above the blocks in wrong place
     */
    private fun spawnParticlesInWrongTerrain() {
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

    /**
     * Pulls the mobs spawned by the waves back to the center
     */
    private fun pullMobsInBorders() {
        val posVector = pos.toVec3d()
        currentTrial?.waves
            ?.get(currentWave)
            ?.spawnedEntities
            ?.filter(LivingEntity::isAlive)
            ?.filter {
                val squaredDistance = it.squaredDistanceTo(posVector.x, posVector.y, posVector.z)
                squaredDistance >=  EFFECTIVE_AREA_RADIUS_SQUARED - 9
            }?.forEach {
                val vector = it.posVector.subtract(pos.toVec3d()).multiply(-0.1)
                it.addVelocity(vector.x, vector.y, vector.z)
            }
    }

    private fun dropRewards() {
        currentTrial?.recipe?.copyRewards()?.map {
            ItemEntity(world, pos.x.toDouble(), pos.y + 1.0, pos.z.toDouble(), it)
        }?.forEach {
            world?.spawnEntity(it)
        }
    }

    private fun startCurrentWave() {
        sendTrialUpdatePackets()
        currentTrial?.waves?.get(currentWave)?.spawnWave(this)
    }

    override fun setLocation(world: World, pos: BlockPos) {
        super.setLocation(world, pos)
        circleBounds = getCircleBoundBlocks()
    }

    private fun areIntegrantsAround() = pos.toVec3d().let { posVec ->
        players.orEmpty().any {
            it.squaredDistanceTo(posVec.x, posVec.y, posVec.z) <= EFFECTIVE_AREA_RADIUS_SQUARED
        }
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

    fun sendTrialUpdatePackets() {
        TrialPlayerData(players?.size ?: 0, currentWave, currentTrial?.waves?.size ?: 0)
            .let { data ->
                players?.forEach { player ->
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                        player,
                        TRIAL_UPDATED_PACKET,
                        data.toPacketByteBuff()
                    )
                }
            }
    }

    fun sendTrialEndPackets(reason: TrialEndReason) {
        PacketByteBuf(Unpooled.buffer())
            .writeString(reason.toString())
            .let { packet ->
                players?.forEach { player ->
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                        player,
                        TRIAL_ENDED_PACKET,
                        packet
                    )
                }
            }
    }

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