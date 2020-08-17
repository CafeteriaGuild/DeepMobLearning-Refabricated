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

import dev.nathanpb.dml.armor.modular.core.EffectStackOption
import dev.nathanpb.dml.armor.modular.core.ModularEffect
import dev.nathanpb.dml.armor.modular.core.ModularEffectContext
import dev.nathanpb.dml.armor.modular.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.context.PlayerEntityTickEvent
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier

abstract class StatusEffectLikeEffect(
    id: Identifier,
    category: EntityCategory,
    isEnabled: ()->Boolean,
    applyCost: ()->Float,
    val stackingOption: EffectStackOption
) : ModularEffect<ModularEffectTriggerPayload>(id, category, isEnabled, applyCost) {

    override fun registerEvents() {
        PlayerEntityTickEvent.register { player ->
            if (player.world.time % 80 == 0L) {
                ModularEffectContext.from(player)
                    .run(stackingOption.apply)
                    .firstOrNull {
                        attemptToApply(it, ModularEffectTriggerPayload.EMPTY) { context, _ ->
                            player.addStatusEffect(createEffectInstance(context))
                        }.result == ActionResult.SUCCESS
                    }
            }
        }
    }

    abstract fun createEffectInstance(context: ModularEffectContext): StatusEffectInstance

    override fun canApply(context: ModularEffectContext, payload: ModularEffectTriggerPayload): Boolean {
        val doesNotHasEffect by lazy {
            val statusEffectInstance = createEffectInstance(context)
            context.player.statusEffects.none {
                it.duration < 15 * 20
                    && it.effectType == statusEffectInstance.effectType
                    && it.amplifier < statusEffectInstance.amplifier
            }
        }

        return super.canApply(context, payload)
            && doesNotHasEffect
            && sumLevelsOf(context.armor.stack) > 0
    }

}
