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
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.context.PlayerEntityTickEvent
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.util.Identifier

abstract class StatusEffectLikeEffect(
    id: Identifier,
    category: EntityCategory,
    isEnabled: ()->Boolean,
    applyCost: ()->Float
) : ModularEffect(id, category, isEnabled, applyCost) {

    override fun registerEvents() {
        PlayerEntityTickEvent.register { player ->
            if (player.world.time % 80 == 0L) {
                val statusEffectInstance = createEffectInstance()
                val shouldRefill = player.statusEffects.none {
                    it.duration < 15 * 20
                        && it.effectType == statusEffectInstance.effectType
                        && it.amplifier < statusEffectInstance.amplifier
                }

                if (shouldRefill && getProtectionAmount(player.armorItems.toList()) > 0) {
                    player.addStatusEffect(statusEffectInstance)
                }
            }
        }
    }

    abstract fun createEffectInstance(): StatusEffectInstance

}
