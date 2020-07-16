/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.trial

import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone
import dev.nathanpb.dml.data.TrialPlayerData
import dev.nathanpb.dml.event.TrialEndCallback
import dev.nathanpb.dml.net.TRIAL_ENDED_PACKET
import dev.nathanpb.dml.net.TRIAL_UPDATED_PACKET
import dev.nathanpb.dml.utils.toVec3d
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.boss.BossBar
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.TranslatableText
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class Trial (
    val world: World,
    val pos: BlockPos,
    val data: TrialData,
    val players: List<PlayerEntity>
) : Tickable {

    constructor(keystone: BlockEntityTrialKeystone, data: TrialData, players: List<PlayerEntity>): this(
        keystone.world!!, // TODO why is World nullable? Assert this
        keystone.pos,
        data,
        players
    )

    companion object {
        val RUNNING_TRIALS = mutableListOf<Trial>()
        const val POST_END_TIMEOUT = 60

        val BAR_TEXT by lazy {
            TranslatableText("bar.deepmoblearning.trial")
        }

        val BAR_TEXT_SUCCESS by lazy {
            TranslatableText("bar.deepmoblearning.trial_success")
        }

        val BAR_TEXT_FAIL by lazy {
            TranslatableText("bar.deepmoblearning.trial_fail")
        }
    }

    var currentWave = 0
        private set

    var state: TrialState = TrialState.NOT_STARTED
        private set


    private var tickCount = 0
    private var endsAt = 0
    val waves = data.waves.map(::TrialWave)
    private val bar = ServerBossBar(BAR_TEXT, BossBar.Color.BLUE, BossBar.Style.NOTCHED_10).also {
        it.isVisible = false
        players
            .filterIsInstance<ServerPlayerEntity>()
            .forEach(it::addPlayer)
    }

    override fun tick() {
        if (!world.isClient && state != TrialState.NOT_STARTED) {
            when (state) {
                TrialState.RUNNING -> {
                    if (currentWave < waves.size) {
                        val wave = waves[currentWave]
                        wave.spawnedEntities.filter(LivingEntity::isAlive).size.let { alive ->
                            bar.percent = MathHelper.clamp(alive / wave.waveData.entityCount.toFloat(), 0F, 1F)
                        }
                        if (!wave.isSpawned) {
                            // Will spawn the current wave if its not spawned yet
                            // Applied when the last tick just moved to the next wave but did not spawned it yet
                            startCurrentWave()
                        } else if (wave.isFinished()) {
                            // Will move to the next wave in the current tick
                            // The next tick will check if the wave exists and
                            // spawn the wave if so, or finish the trial if not
                            currentWave++
                            if (currentWave >= waves.size) {
                                end(TrialEndReason.SUCCESS)
                            }
                        }
                    } else {
                        // Just as fallback if somehow the last wave was skipped
                        end(TrialEndReason.SUCCESS)
                    }
                }
                TrialState.WAITING_POST_FINISHED -> {
                    if (tickCount >= endsAt) {
                        state = TrialState.FINISHED
                        bar.isVisible = false
                    }
                }
                else -> {} // Suppress non-exhaustive when
            }
            tickCount++
        }
    }

    /*
     * Trial Control
     */

    fun start() {
        if (state == TrialState.NOT_STARTED) {
            state = TrialState.RUNNING
            RUNNING_TRIALS += this
            bar.isVisible = true
            sendTrialUpdatePackets()
        } else throw TrialKeystoneIllegalStartException(this)
    }

    fun end(reason: TrialEndReason) {
        if (state == TrialState.RUNNING) {
            when (reason) {
                TrialEndReason.SUCCESS -> {
                    bar.color = BossBar.Color.GREEN
                    bar.name = BAR_TEXT_SUCCESS

                    dropRewards()
                }
                TrialEndReason.NO_ONE_IS_AROUND -> {
                    bar.color = BossBar.Color.RED
                    bar.name = BAR_TEXT_FAIL

                    players.forEach {
                        it.playSound(SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.BLOCKS, 1F, 1F)
                    }

                    waves[currentWave].despawnWave()
                }
            }

            endsAt = tickCount + POST_END_TIMEOUT
            bar.percent = 1F
            RUNNING_TRIALS -= this
            state = TrialState.WAITING_POST_FINISHED
            sendTrialEndPackets(reason)
            TrialEndCallback.EVENT.invoker().onTrialEnd(this, reason)
        } else throw TrialKeystoneIllegalEndException(this)
    }

    private fun dropRewards() {
        data.recipe.copyRewards().map {
            pos.toVec3d().run {
                ItemEntity(world, x, y + 1, z, it)
            }
        }.forEach {
            world.spawnEntity(it)
        }
    }

    private fun startCurrentWave() {
        sendTrialUpdatePackets()
        waves[currentWave].spawnWave(world, pos)
    }


    /*
     * Networking
     */

    private fun sendTrialUpdatePackets() {
        TrialPlayerData(players.size, currentWave, data.waves.size)
            .let { data ->
                players.forEach { player ->
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                        player,
                        TRIAL_UPDATED_PACKET,
                        data.toPacketByteBuff()
                    )
                }
            }
    }

    private fun sendTrialEndPackets(reason: TrialEndReason) {
        PacketByteBuf(Unpooled.buffer())
            .writeString(reason.toString())
            .let { packet ->
                players.forEach { player ->
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                        player,
                        TRIAL_ENDED_PACKET,
                        packet
                    )
                }
            }
    }

}
