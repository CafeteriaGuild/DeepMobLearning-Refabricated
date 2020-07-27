/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.trial

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone
import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.EntityCategory
import dev.nathanpb.dml.entity.SYSTEM_GLITCH_ENTITY_TYPE
import dev.nathanpb.dml.entity.SystemGlitchEntity
import dev.nathanpb.dml.event.TrialEndCallback
import dev.nathanpb.dml.item.ITEM_EMERITUS_HAT
import dev.nathanpb.dml.recipe.TrialKeystoneRecipe
import dev.nathanpb.dml.utils.*
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.boss.BossBar
import net.minecraft.entity.boss.ServerBossBar
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.TranslatableText
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.random.Random

class Trial (
    val world: World,
    val pos: BlockPos,
    val recipe: TrialKeystoneRecipe,
    val players: List<PlayerEntity>
) : Tickable {

    constructor(keystone: BlockEntityTrialKeystone, recipe: TrialKeystoneRecipe, players: List<PlayerEntity>): this(
        keystone.world!!, // TODO why is World nullable? Assert this
        keystone.pos,
        recipe,
        players
    )

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
    }

    var systemGlitch: SystemGlitchEntity? = null
        private set

    var state: TrialState = TrialState.NOT_STARTED
        private set


    private var tickCount = 0
    private var endsAt = 0
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

    fun start() {
        if (state == TrialState.NOT_STARTED) {
            state = TrialState.RUNNING
            spawnSystemGlitch()
            world.runningTrials += this
            bar.isVisible = true
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
                }
            }

            endsAt = tickCount + config.trial.postEndTimeout
            bar.percent = 1F
            world.runningTrials -= this
            state = TrialState.WAITING_POST_FINISHED
            systemGlitch?.remove()
            TrialEndCallback.EVENT.invoker().onTrialEnd(this, reason)
        } else throw TrialKeystoneIllegalEndException(this)
    }

    private fun spawnSystemGlitch() {
        systemGlitch = SYSTEM_GLITCH_ENTITY_TYPE.spawn(
            world, null, null, null, pos.add(0, 2, 0), SpawnReason.EVENT, false, false
        )?.also {
            it.tier = recipe.tier
            it.health = it.maxHealth
            if (recipe.category == EntityCategory.GHOST && recipe.tier.isMaxTier() && Random.nextFloat() < .0666 ) {
                it.equipStack(EquipmentSlot.HEAD, ItemStack(ITEM_EMERITUS_HAT))
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
        (0 until recipe.waveEntityCount).forEach { _ ->
            recipe.category.tag.getRandom(java.util.Random()).spawn(
                world,
                null, null, null,
                pos.add(Random.nextInt(-2, 2), 5, Random.nextInt(-2, 2)),
                SpawnReason.SPAWNER,
                false, false
            )
        }
    }

    fun getMonstersInArena(): List<HostileEntity> {
        return world.getEntitiesAroundCircle(null, pos, config.trial.arenaRadius.squared().toDouble())
            .filterIsInstance<HostileEntity>()
    }
}
