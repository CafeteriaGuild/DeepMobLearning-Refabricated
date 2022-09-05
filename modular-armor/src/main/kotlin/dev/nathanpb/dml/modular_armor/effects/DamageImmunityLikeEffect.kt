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

import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.VanillaEvents
import dev.nathanpb.dml.modular_armor.core.ModularEffectContext
import dev.nathanpb.dml.modular_armor.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.modular_armor.core.WrappedEffectTriggerPayload
import dev.nathanpb.dml.utils.takeOrNull
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier

abstract class DamageImmunityLikeEffect(
    id: Identifier,
    category: EntityCategory,
    applyCost: ()->Float
) : ProtectionLikeEffect(id, category, applyCost) {

    override fun registerEvents() {
        VanillaEvents.LivingEntityDamageEvent.register { eventContext ->
            takeOrNull(eventContext.entity is PlayerEntity) {
                ModularEffectContext.from(eventContext.entity as PlayerEntity)
                    .shuffled()
                    .firstOrNull { effectContext ->
                        attemptToApply(effectContext, ModularEffectTriggerPayload.wrap(eventContext)) == ActionResult.SUCCESS
                    }?.let {
                        eventContext.copy(damage = 0F)
                    }
            }
        }
    }

    override fun canApply(context: ModularEffectContext, payload: WrappedEffectTriggerPayload<VanillaEvents.LivingEntityDamageContext>): Boolean {
        return super.canApply(context, payload) && sumLevelsOf(context.armor.stack) > 0
    }
}
