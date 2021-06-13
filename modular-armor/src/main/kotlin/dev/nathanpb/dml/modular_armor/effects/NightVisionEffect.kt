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

import dev.nathanpb.dml.config
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.modular_armor.core.EffectStackOption
import dev.nathanpb.dml.modular_armor.core.ModularEffectContext
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.text.TranslatableText

class NightVisionEffect : StatusEffectLikeEffect(
    identifier("night_vision"),
    EntityCategory.GHOST,
    config.glitchArmor.costs::nightVision,
    EffectStackOption.RANDOMIZE
) {

    override val name = TranslatableText("effect.minecraft.night_vision")

    override fun createEffectInstance(context: ModularEffectContext): StatusEffectInstance {
        return StatusEffectInstance(StatusEffects.NIGHT_VISION, 16 * 20, 0, true, false)
    }

    override fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(name.key, 1.0, EntityAttributeModifier.Operation.MULTIPLY_BASE)
    }

    override fun acceptTier(tier: DataModelTier): Boolean {
        return tier.ordinal >= 1
    }

}
