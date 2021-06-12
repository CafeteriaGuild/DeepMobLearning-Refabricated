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

package dev.nathanpb.dml.armor.modular.cooldown

import dev.nathanpb.dml.armor.modular.core.EffectNotFoundException
import dev.nathanpb.dml.armor.modular.core.ModularEffectContext
import dev.nathanpb.dml.armor.modular.core.ModularEffectRegistry
import dev.nathanpb.dml.armor.modular.effects.FlyEffect
import dev.nathanpb.dml.config
import dev.nathanpb.dml.net.S2C_FLIGHT_BURNOUT_MANAGER_UPDATE
import dev.nathanpb.dml.utils.firstInstanceOrNull
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World
import kotlin.math.max

class FlightBurnoutManager(private val player: net.minecraft.entity.player.PlayerEntity) {

    val world: World? = player.world

    private val effect by lazy {
        ModularEffectRegistry.INSTANCE.all.firstInstanceOrNull<FlyEffect>() ?: throw EffectNotFoundException()
    }

    // This value is automagically updated every 20 ticks
    // to keep synchronization and avoid heavy calculations every tick
    private var maxFlightTicks: Int = 0

    private var burnoutTicks = 0

    var canFly = true
        private set


    fun tick() {
        if (
            world != null
            && !world.isClient
            && config.glitchArmor.maxFlightTicksPerLevel > 0
            && !(player.isCreative || player.isSpectator)
        ) {

            if (world.time % 20 == 0L) {
                maxFlightTicks = config.glitchArmor.maxFlightTicksPerLevel * effect.sumLevelsOf(
                    ModularEffectContext.from(player).map { it.armor.stack }
                ).toInt()
            }

            if (world.time % (if (!canFly || player.abilities.flying) 10 else 20) == 0L) {
                sync()
            }

            if (effect.abilitySource.grants(player, effect.ability)) {
                if (player.abilities.flying) {
                    val maxFlightTicks = maxFlightTicks
                    if (maxFlightTicks > 0) {
                        burnoutTicks++
                        if (burnoutTicks >= maxFlightTicks) {
                            canFly = false
                        }
                    }
                }
            }
            if (!player.abilities.flying) {
                if (burnoutTicks > 0) {
                    burnoutTicks = max(0, burnoutTicks-2)
                }
                if (!canFly && burnoutTicks == 0) {
                    canFly = true
                }
            }
        }
    }

    private fun sync() {
        if (player is ServerPlayerEntity) {
            val packet = PacketByteBuf(Unpooled.buffer()).apply {
                writeInt(burnoutTicks)
                writeInt(maxFlightTicks)
                writeBoolean(canFly)
            }
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, S2C_FLIGHT_BURNOUT_MANAGER_UPDATE, packet)
        }

    }

}
