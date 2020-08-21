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

import dev.nathanpb.dml.armor.modular.core.*
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.context.CanTargetEntityEvent
import dev.nathanpb.dml.utils.firstOrNullMapping
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier

abstract class TargetCancellationEffect(
    id: Identifier,
    category: EntityCategory,
    applyCost: () -> Float
) : ModularEffect<WrappedEffectTriggerPayload<LivingEntity>>(id, category, applyCost) {

    override fun registerEvents() {
        CanTargetEntityEvent.register { mob, target ->

            if (target is PlayerEntity) {
                ModularEffectContext.from(target)
                    .run(EffectStackOption.RANDOMIZE.apply)
                    .firstOrNullMapping ({ context ->
                        attemptToApply(context, ModularEffectTriggerPayload.wrap(mob))
                    }, ActionResult.SUCCESS::equals)?.let {
                        return@register ActionResult.FAIL
                    }
            }

            return@register ActionResult.PASS
        }
    }

    override fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(id.toString(), 1.0, EntityAttributeModifier.Operation.MULTIPLY_BASE)
    }

    // Make sure it will only cancel the target when absolutely needed
    override fun canApply(context: ModularEffectContext, payload: WrappedEffectTriggerPayload<LivingEntity>): Boolean {
        val entity = payload.value
        return entity.isAlive
            && entity.canTarget(entity.type)
            && super.canApply(context, payload)
            && entity.distanceTo(context.player) <= entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE)
    }

    // Data draining goes brrrrrr
    override fun shouldConsumeData(context: ModularEffectContext): Boolean {
        return super.shouldConsumeData(context) && context.player.world.time % 20 == 0L
    }

}
