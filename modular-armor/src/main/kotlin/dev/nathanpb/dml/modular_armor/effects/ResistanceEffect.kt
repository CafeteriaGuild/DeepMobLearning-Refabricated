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
import dev.nathanpb.dml.event.LivingEntityDamageContext
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.damage.DamageSource
import kotlin.math.min

class ResistanceEffect : ProtectionLikeEffect(
    identifier("resistance"),
    EntityCategory.ILLAGER,
    config.glitchArmor.costs::resistance
) {

    override fun protectsAgainst(source: DamageSource): Boolean {
        return !source.isUnblockable && !source.isMagic
    }

    override fun acceptTier(tier: DataModelTier) = true

    override fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(id.toString(), armor.tier().ordinal.inc() / 20.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
    }

    override fun inflictDamage(event: LivingEntityDamageContext, armorValues: Double): Float {
        return event.damage * (1 - min(0.25, armorValues)).toFloat()
    }

}
