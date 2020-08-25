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

package dev.nathanpb.dml.armor.modular.effects

import dev.nathanpb.dml.armor.modular.core.*
import dev.nathanpb.dml.config
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.utils.`if`
import dev.nathanpb.dml.utils.firstInstanceOrNull
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult

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
                }
        }
    }

    override fun registerEvents() {

    }

    override fun acceptTier(tier: DataModelTier) = tier.isMaxTier()
}
