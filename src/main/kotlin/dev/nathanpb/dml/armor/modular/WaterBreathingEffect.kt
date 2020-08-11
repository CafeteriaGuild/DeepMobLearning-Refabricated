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
import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.identifier
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.text.TranslatableText

class WaterBreathingEffect : ModularEffect(
    identifier("water_breathing"),
    EntityCategory.OCEAN,
    config.glitchArmor::enableWaterBreathing,
    config.glitchArmor::waterBreathingCost
) {

    override val name = TranslatableText("effect.minecraft.water_breathing")

    override fun registerEvents() {

    }

    override fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(name.key, 1.0, EntityAttributeModifier.Operation.MULTIPLY_BASE)
    }

    override fun shouldConsumeData(context: ModularEffectContext) = true

    override fun acceptTier(tier: DataModelTier): Boolean {
        return tier.ordinal >= 3
    }

}
