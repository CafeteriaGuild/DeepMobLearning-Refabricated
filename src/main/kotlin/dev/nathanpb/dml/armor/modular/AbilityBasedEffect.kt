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
import io.github.ladysnake.pal.AbilitySource
import io.github.ladysnake.pal.Pal
import io.github.ladysnake.pal.PlayerAbility
import net.minecraft.util.Identifier

abstract class AbilityBasedEffect(
    id: Identifier,
    category: EntityCategory,
    isEnabled: () -> Boolean,
    applyCost: () -> Float,
    val ability: PlayerAbility
) : ModularEffect<ModularEffectTriggerPayload>(id, category, isEnabled, applyCost) {

    val abilitySource: AbilitySource = Pal.getAbilitySource(id)

    override fun registerEvents() {
        PlayerEntityTickEvent.register { player ->

            // Checking if the player should have the ability
            // This does not consume any data
            if (!player.world.isClient && player.world.time % 20 == 0L) {
                ModularEffectContext.from(player)
                    .run(EffectStackOption.PRIORITIZE_GREATER.apply)
                    .any { context ->
                        canApply(context, ModularEffectTriggerPayload.EMPTY)
                    }.let { shallApply ->
                        val has = abilitySource.grants(player, ability)
                        if (shallApply && !has) {
                            abilitySource.grantTo(player, ability)
                        } else if (!shallApply && has) {
                            abilitySource.revokeFrom(player, ability)
                        }
                    }
            }
        }
    }

}
