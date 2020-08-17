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

package dev.nathanpb.dml.armor.modular

import dev.nathanpb.dml.armor.modular.core.ModularEffect
import dev.nathanpb.dml.armor.modular.core.ModularEffectContext
import dev.nathanpb.dml.armor.modular.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.armor.modular.core.WrappedEffectTriggerPayload
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.context.PlayerEntityDamageContext
import dev.nathanpb.dml.event.context.PlayerEntityDamageEvent
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.damage.DamageSource
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier

abstract class ProtectionLikeEffect(
    id: Identifier,
    category: EntityCategory,
    isEnabled: ()->Boolean,
    applyCost: ()->Float
) : ModularEffect<WrappedEffectTriggerPayload<PlayerEntityDamageContext>>(id, category, isEnabled, applyCost) {

    override fun registerEvents() {
        PlayerEntityDamageEvent.register { eventContext ->
            if (!eventContext.entity.world.isClient) {
                val armorValues = ModularEffectContext.from(eventContext.entity).fold(0) { acc, effectContext ->
                    val result = attemptToApply(effectContext, ModularEffectTriggerPayload.wrap(eventContext)) { _, _ ->
                        sumLevelsOf(effectContext.armor.stack)
                    }

                    if (result.result == ActionResult.SUCCESS) {
                        acc + result.value
                    } else acc
                }
                return@register eventContext.copy(
                    damage = DamageUtil.getInflictedDamage(eventContext.damage, armorValues.toFloat())
                )
            }
            return@register null
        }
    }

    abstract fun protectsAgainst(source: DamageSource): Boolean

    override fun canApply(context: ModularEffectContext, payload: WrappedEffectTriggerPayload<PlayerEntityDamageContext>): Boolean {
        return super.canApply(context, payload) && protectsAgainst(payload.value.source)
    }

}