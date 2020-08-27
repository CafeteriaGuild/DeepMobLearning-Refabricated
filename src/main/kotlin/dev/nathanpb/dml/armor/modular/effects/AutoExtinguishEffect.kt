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

import dev.nathanpb.dml.armor.modular.ProtectionLikeEffect
import dev.nathanpb.dml.armor.modular.core.ModularEffectContext
import dev.nathanpb.dml.armor.modular.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.armor.modular.core.WrappedEffectTriggerPayload
import dev.nathanpb.dml.config
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.context.LivingEntityDamageContext
import dev.nathanpb.dml.event.context.LivingEntityDamageEvent
import dev.nathanpb.dml.identifier
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult

class AutoExtinguishEffect : ProtectionLikeEffect(
    identifier("auto_extinguish"),
    EntityCategory.NETHER,
    config.glitchArmor.costs::autoExtinguish
) {
    override fun registerEvents() {
        LivingEntityDamageEvent.register { eventContext ->
            if (eventContext.entity is PlayerEntity) {
                ModularEffectContext.from(eventContext.entity)
                    .shuffled()
                    .firstOrNull { effectContext ->
                        attemptToApply(effectContext, ModularEffectTriggerPayload.wrap(eventContext)) { _, _ ->
                            eventContext.entity.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1F, 1F)
                            eventContext.entity.fireTicks = 0
                        }.result == ActionResult.SUCCESS
                    }
            }
            null
        }
    }

    override fun protectsAgainst(source: DamageSource) = source.isFire

    override fun acceptTier(tier: DataModelTier): Boolean {
        return tier.ordinal >= 2
    }

    // todo check if the player is standing in fire too
    override fun canApply(context: ModularEffectContext, payload: WrappedEffectTriggerPayload<LivingEntityDamageContext>): Boolean {
        return super.canApply(context, payload) && !context.player.isInLava
    }

}
