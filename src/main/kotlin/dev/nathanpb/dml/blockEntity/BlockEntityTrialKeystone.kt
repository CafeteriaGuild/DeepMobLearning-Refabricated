/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.block.BLOCK_TRIAL_KEYSTONE
import dev.nathanpb.dml.event.TrialEndCallback
import dev.nathanpb.dml.trial.*
import dev.nathanpb.dml.utils.getEntitiesAroundCircle
import dev.nathanpb.dml.utils.toVec3d
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.particle.ParticleTypes
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
    TrialEndCallback,
    BlockEntityClientSerializable,
    Tickable
{

    companion object {
        const val EFFECTIVE_AREA_RADIUS = 12.0
        const val EFFECTIVE_AREA_RADIUS_SQUARED = EFFECTIVE_AREA_RADIUS * EFFECTIVE_AREA_RADIUS
        val BORDERS_RANGE = (EFFECTIVE_AREA_RADIUS_SQUARED - 9) .. (EFFECTIVE_AREA_RADIUS_SQUARED + 9)
    }

    private var circleBounds: List<BlockPos>? = null
    var currentTrial: Trial? = null

    var clientTrialState = TrialState.NOT_STARTED

    init {
        TrialEndCallback.EVENT.register(this)
    }

    override fun onTrialEnd(trial: Trial, reason: TrialEndReason) {
        if (currentTrial == trial) {
            sync()
            trial.world.blockTickScheduler.schedule(pos, BLOCK_TRIAL_KEYSTONE, Trial.POST_END_TIMEOUT + 1)
        }
    }

    override fun tick() {
        if (world?.isClient == true) {
            when (clientTrialState) {
                TrialState.NOT_STARTED -> {
                    MinecraftClient.getInstance().player?.let { clientPlayer ->
                        if (clientPlayer.squaredDistanceTo(pos.toVec3d()) <= EFFECTIVE_AREA_RADIUS_SQUARED) {
                            spawnParticlesInWrongTerrain()
                        }
                    }
                }
                TrialState.RUNNING -> {
                    pullMobsInBorders(listOf(MinecraftClient.getInstance().player as LivingEntity))
                }
                else -> {}
            }
            return
        }
        currentTrial?.let { trial ->
            val state = currentTrial?.state ?: TrialState.NOT_STARTED
            if (state != TrialState.NOT_STARTED && state != TrialState.FINISHED) {
                if (state == TrialState.RUNNING) {
                    pullMobsInBorders(trial.waves[trial.currentWave].spawnedEntities)
                    if (!arePlayersAround(trial.players)) {
                        trial.end(TrialEndReason.NO_ONE_IS_AROUND)
                    }
                }
                trial.tick()
            }
        }
    }

    fun createTrial(data: TrialData): Trial {
        val players = world?.getEntitiesAroundCircle(EntityType.PLAYER, pos, EFFECTIVE_AREA_RADIUS).orEmpty()
        if (players.isNotEmpty()) {
            return Trial(this, data, players)
        } else throw TrialKeystoneNoPlayersAround(this)
    }

    fun startTrial(trial: Trial) {
        checkTerrain().let { wrongTerrain ->
            if (wrongTerrain.isEmpty()) {
                currentTrial = trial
                trial.start()
                sync()
            } else throw TrialKeystoneWrongTerrainException(this, wrongTerrain)
        }
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
    private fun pullMobsInBorders(mobs: List<LivingEntity>) {
        val posVector = pos.toVec3d()
        mobs.filter(LivingEntity::isAlive)
            .filter {
                val squaredDistance = it.squaredDistanceTo(posVector.x, posVector.y, posVector.z)
                squaredDistance in BORDERS_RANGE
            }.forEach {
                val vector = it.pos.subtract(pos.toVec3d()).multiply(-0.1)
                it.addVelocity(vector.x, vector.y, vector.z)
            }
    }

    override fun setLocation(world: World, pos: BlockPos) {
        super.setLocation(world, pos)
        circleBounds = getCircleBoundBlocks()
    }

    private fun arePlayersAround(players: List<PlayerEntity>) = pos.toVec3d().let { posVec ->
        players.any {
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

    override fun toClientTag(tag: CompoundTag) = tag.also {
        it.putString("deepmoblearning:state", (currentTrial?.state ?: TrialState.NOT_STARTED).name)
    }

    override fun fromClientTag(tag: CompoundTag) {
        clientTrialState = tag.getString("deepmoblearning:state").let { name ->
            if (name.isNotEmpty())  TrialState.valueOf(name) else TrialState.NOT_STARTED
        }
    }
}
