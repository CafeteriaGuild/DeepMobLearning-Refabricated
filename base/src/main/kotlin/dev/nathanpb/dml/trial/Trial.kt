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

package dev.nathanpb.dml.trial

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone
import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.TrialData
import dev.nathanpb.dml.entity.SYSTEM_GLITCH_ENTITY_TYPE
import dev.nathanpb.dml.entity.SystemGlitchEntity
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.TrialEndEvent
import dev.nathanpb.dml.event.TrialStateChanged
import dev.nathanpb.dml.event.TrialWaveSpawnEvent
import dev.nathanpb.dml.item.ITEM_EMERITUS_HAT
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import dev.nathanpb.dml.trial.affix.core.TrialAffix
import dev.nathanpb.dml.utils.*
import net.minecraft.entity.*
import net.minecraft.entity.boss.BossBar
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.TranslatableText
import net.minecraft.util.TypeFilter
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import kotlin.random.Random

class Trial (
    val world: World,
    val pos: BlockPos,
    val recipe: TrialKeystoneRecipe,
    val players: HashSet<UUID>,
    val affixes: List<TrialAffix>
) {

    constructor(
        keystone: BlockEntityTrialKeystone,
        recipe: TrialKeystoneRecipe,
        players: List<UUID>,
        affixes: List<TrialAffix>
    ): this(
        keystone.world!!, // TODO why is World nullable? Assert this
        keystone.pos,
        recipe,
        players.toHashSet(),
        affixes
    )

    constructor(keystone: BlockEntityTrialKeystone, recipe: TrialKeystoneRecipe, data: TrialData): this(
        keystone,
        recipe,
        data.playersUuids,
        data.affixes
    ) {
        state = data.state
        tickCount = data.tickCount
        if (state in arrayOf(TrialState.WARMUP, TrialState.RUNNING)) {
            start(true)
        }
        systemGlitch?.health = data.glitchHealth
        systemGlitch?.tier = recipe.tier
    }

    companion object {
        val BAR_TEXT by lazy {
            TranslatableText("bar.${MOD_ID}.trial")
        }

        val BAR_TEXT_SUCCESS by lazy {
            TranslatableText("bar.${MOD_ID}.trial_success")
        }

        val BAR_TEXT_FAIL by lazy {
            TranslatableText("bar.${MOD_ID}.trial_fail")
        }
        val BAR_TEXT_FAIL_TIMEOUT by lazy {
            TranslatableText("bar.${MOD_ID}.trial_fail.timeout")
        }
    }

    var systemGlitch: SystemGlitchEntity? = null
        private set

    var state: TrialState = TrialState.NOT_STARTED
        private set

    private val tickableAffixes = affixes.filterIsInstance<TrialAffix.TickableAffix>()


    var tickCount = 0; private set
    private val bar = ServerBossBar(BAR_TEXT, BossBar.Color.BLUE, BossBar.Style.NOTCHED_10).also {
        it.isVisible = false
    }

    fun tick() {
        if (!world.isClient && state != TrialState.NOT_STARTED) {
            if (state in arrayOf(TrialState.WARMUP, TrialState.RUNNING)) {
                // Resyncs which players will have the bossbar every second
                if (tickCount % 20 == 0) {
                    bar.clearPlayers()
                    world.getPlayersByUUID(players)
                        .filterIsInstance<ServerPlayerEntity>()
                        .forEach(bar::addPlayer)
                }

                when (state) {
                    TrialState.RUNNING -> {
                        if (tickCount % recipe.waveRespawnTimeout == 0) {
                            if (getMonstersInArena().size < config.trial.maxMobsInArena) {
                                spawnWave()
                            }
                        }

                        systemGlitch?.let { systemGlitch ->
                            if (!config.trial.allowPlayersLeavingArena) {
                                val glitchPos = systemGlitch.pos.toBlockPos()
                                if (glitchPos.y < pos.y || !TrialGriefPrevention.isInArea(pos, glitchPos)) {
                                    pos.toVec3d().apply {
                                        systemGlitch.teleport(x, y, z)
                                    }
                                }
                            }
                            bar.percent = systemGlitch.health / systemGlitch.maxHealth
                        }

                        if (systemGlitch?.isAlive != true) {
                            end(TrialEndReason.SUCCESS)
                        }

                        if (tickCount > config.trial.maxTime) {
                            end(TrialEndReason.TIMED_OUT)
                        }

                        tickableAffixes.forEach {
                            it.tick(this)
                        }
                    }

                    TrialState.WARMUP -> {
                        if (tickCount >= config.trial.warmupTime) {
                            state = TrialState.RUNNING
                            TrialStateChanged.invoker().invoke(this)
                            spawnSystemGlitch()
                            world.getPlayersByUUID(players).forEach {
                                it.playSound(
                                    SoundEvents.BLOCK_NOTE_BLOCK_BASS,
                                    SoundCategory.BLOCKS,
                                    1F, 1F
                                )
                            }
                        } else if (tickCount % 20 == 0) {
                            world.getPlayersByUUID(players).forEach {
                                it.playSound(
                                    SoundEvents.BLOCK_NOTE_BLOCK_BASS,
                                    SoundCategory.BLOCKS,
                                    1.5F, .75F
                                )
                            }
                        }
                    }
                    else -> {} // Suppress non-exhaustive when
                }
            }
            tickCount++
        }
    }

    fun start(forceStart: Boolean = false) {
        if (forceStart || state == TrialState.NOT_STARTED) {
            state = TrialState.WARMUP
            TrialStateChanged.invoker().invoke(this)
            world.runningTrials += this
            bar.isVisible = true
        } else throw TrialKeystoneIllegalStartException(this)
    }

    fun end(reason: TrialEndReason) {
        if (state == TrialState.RUNNING) {
            when (reason) {
                TrialEndReason.SUCCESS -> {
                    world.getPlayersByUUID(players).forEach {
                        it.sendMessage(BAR_TEXT_SUCCESS, false)
                    }
                    dropRewards()
                }
                TrialEndReason.NO_ONE_IS_AROUND -> {
                    world.getPlayersByUUID(players).forEach {
                        it.sendMessage(BAR_TEXT_FAIL, false)
                    }
                    playFailSounds()
                }
                TrialEndReason.TIMED_OUT -> {
                    world.getPlayersByUUID(players).forEach {
                        it.sendMessage(BAR_TEXT_FAIL_TIMEOUT, false)
                    }
                    playFailSounds()
                }
            }

            bar.isVisible = false
            world.runningTrials -= this
            state = TrialState.FINISHED
            systemGlitch?.remove(Entity.RemovalReason.DISCARDED)
            TrialStateChanged.invoker().invoke(this)
            TrialEndEvent.invoker().invoke(this, reason)
        } else throw TrialKeystoneIllegalEndException(this)
    }

    private fun playFailSounds() {
        world.getPlayersByUUID(players).forEach {
            it.playSound(SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.BLOCKS, 1F, 1F)
        }
    }

    private fun spawnSystemGlitch() {
        (world as? ServerWorld)?.let { world ->
            systemGlitch = SYSTEM_GLITCH_ENTITY_TYPE.spawn(
                world, null, null, null, pos.add(0, 2, 0), SpawnReason.EVENT, false, false
            )?.also {
                it.tier = recipe.tier
                it.health = it.maxHealth
                if (recipe.category == EntityCategory.GHOST && recipe.tier.isMaxTier() && Random.nextFloat() < .0666 ) {
                    it.equipStack(EquipmentSlot.HEAD, ItemStack(ITEM_EMERITUS_HAT))
                }
                it.belongsToTrial = true
            }
        }
    }

    private fun dropRewards() {
        recipe.copyRewards().map {
            pos.toVec3d().run {
                ItemEntity(world, x, y + 1, z, it)
            }
        }.forEach {
            world.spawnEntity(it)
        }
    }

    private fun spawnWave() {
        (world as? ServerWorld)?.let { world ->
            (0 until recipe.waveEntityCount).map {
                recipe.category.tag.getRandom(java.util.Random()).spawn(
                    world,
                    null, null, null,
                    pos.add(Random.nextInt(-2, 2), 5, Random.nextInt(-2, 2)),
                    SpawnReason.SPAWNER,
                    false, false
                )
            }.let {
                TrialWaveSpawnEvent.invoker().invoke(
                    this,
                    it.filterIsInstance<LivingEntity>()
                )
            }
        }
    }

    fun getMonstersInArena(): List<HostileEntity> {
        return world.getEntitiesAroundCircle(
            TypeFilter.instanceOf(HostileEntity::class.java),
            pos, config.trial.arenaRadius.squared().toDouble()
        )
    }
}
