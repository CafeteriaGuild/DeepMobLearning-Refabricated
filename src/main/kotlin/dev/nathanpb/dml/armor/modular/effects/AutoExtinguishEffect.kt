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
import dev.nathanpb.dml.config
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.context.PlayerEntityDamageEvent
import dev.nathanpb.dml.identifier
import net.minecraft.entity.damage.DamageSource
import net.minecraft.sound.SoundEvents

class AutoExtinguishEffect : ProtectionLikeEffect(
    identifier("auto_extinguish"),
    EntityCategory.NETHER,
    config.glitchArmor::enableAutoExtinguish,
    config.glitchArmor::autoExtinguishCost
) {
    override fun registerEvents() {
        PlayerEntityDamageEvent.register { context ->
            if (protectsAgainst(context.source) && !context.entity.isInLava) {
                val protection = sumLevelsOf(context.entity.armorItems.toList())
                if (protection > 0) {
                    context.entity.fireTicks = 0
                    context.entity.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1F, 1F)
                    return@register context.copy(damage = 0F)
                }
            }
            null
        }
    }

    override fun protectsAgainst(source: DamageSource) = source.isFire

    override fun shouldConsumeData(context: ModularEffectContext) = true

    override fun acceptTier(tier: DataModelTier): Boolean {
        return tier.ordinal >= 2
    }

}
