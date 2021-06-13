/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.modular_armor.effects

import dev.nathanpb.dml.config
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.PlayerEntityTickEvent
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.modular_armor.core.*
import dev.nathanpb.dml.modular_armor.net.S2C_UNDYING_COOLDOWN_UPDATE
import dev.nathanpb.dml.modular_armor.undyingLastUsage
import dev.nathanpb.dml.utils.`if`
import dev.nathanpb.dml.utils.firstInstanceOrNull
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult
import kotlin.math.max

class UndyingEffect : ModularEffect<ModularEffectTriggerPayload>(
    identifier("undying"),
    EntityCategory.ILLAGER,
    config.glitchArmor.costs::undying
) {

    companion object {
        private val INSTANCE by lazy {
            ModularEffectRegistry.INSTANCE.all.firstInstanceOrNull<UndyingEffect>()
        }

        fun trigger(player: PlayerEntity) = INSTANCE?.run {
            return@run ModularEffectContext.from(player)
                .run(EffectStackOption.RANDOMIZE.apply)
                .any { attemptToApply(it, ModularEffectTriggerPayload.EMPTY) == ActionResult.SUCCESS }
                .`if` {
                    player.undyingLastUsage = player.world.time
                    player.health = 1.0f
                    player.clearStatusEffects()
                    player.addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION, 900, 1))
                    player.addStatusEffect(StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1))
                    player.addStatusEffect(StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0))
                    player.world.sendEntityStatus(player, 35.toByte())

                    if (player is ServerPlayerEntity) {
                        player.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING))
                        Criteria.USED_TOTEM.trigger(player, ItemStack(Items.TOTEM_OF_UNDYING))
                    }
                    syncCooldown(player, config.glitchArmor.undyingCooldownTime)
                    true
                }
        } ?: false
    }

    override fun registerEvents() {
        PlayerEntityTickEvent.register { player ->
            if (!player.world.isClient && player.world.time % 40 == 0L) {
                ModularEffectContext.from(player)
                    .any { super.canApply(it, ModularEffectTriggerPayload.EMPTY) }
                    .let {
                       syncCooldown(player, if (it) config.glitchArmor.undyingCooldownTime else 0)
                    }
            }
        }

    }

    override fun acceptTier(tier: DataModelTier) = tier.isMaxTier()

    private fun remainingCooldownTime(player: PlayerEntity): Int = player.undyingLastUsage?.let {
        max(config.glitchArmor.undyingCooldownTime - (player.world.time - it), 0).toInt()
    } ?: 0

    private fun isUnderCooldown(player: PlayerEntity) : Boolean {
        return remainingCooldownTime(player) > 0
    }

    override fun canApply(context: ModularEffectContext, payload: ModularEffectTriggerPayload): Boolean {
        return super.canApply(context, payload) && !isUnderCooldown(context.player)
    }

    fun syncCooldown(player: PlayerEntity, maxCooldownTime: Int) {
        val packet = PacketByteBuf(Unpooled.buffer()).apply {
            writeInt(remainingCooldownTime(player))
            writeInt(maxCooldownTime)
        }
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, S2C_UNDYING_COOLDOWN_UPDATE, packet)
    }
}
