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

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.armor.modular.StatusEffectLikeEffect
import dev.nathanpb.dml.armor.modular.core.EffectStackOption
import dev.nathanpb.dml.armor.modular.core.ModularEffectContext
import dev.nathanpb.dml.armor.modular.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.config
import dev.nathanpb.dml.entity.effect.DEPTH_STRIDER_EFFECT
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.identifier
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.text.TranslatableText

class DepthStriderEffect : StatusEffectLikeEffect(
    identifier("depth_strider"),
    EntityCategory.OCEAN,
    config.glitchArmor::enableDepthStrider,
    config.glitchArmor::depthStriderCost,
    EffectStackOption.PRIORITIZE_GREATER
) {

    override val name = TranslatableText("effect.${MOD_ID}.depth_strider")

    override fun createEffectInstance(context: ModularEffectContext): StatusEffectInstance {
        return StatusEffectInstance(DEPTH_STRIDER_EFFECT, 16 * 20, context.tier.ordinal / 2, true, false)
    }

    override fun shouldConsumeData(context: ModularEffectContext) = true

    override fun acceptTier(tier: DataModelTier): Boolean {
        return tier.ordinal >= 2
    }

    override fun canApply(context: ModularEffectContext, payload: ModularEffectTriggerPayload): Boolean {
        return super.canApply(context, payload) && context.player.isTouchingWater
    }

}
