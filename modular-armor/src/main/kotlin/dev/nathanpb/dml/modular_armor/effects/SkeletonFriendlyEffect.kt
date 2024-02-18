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

import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.modular_armor.core.ModularEffectContext
import dev.nathanpb.dml.modular_armor.core.WrappedEffectTriggerPayload
import dev.nathanpb.dml.modular_armor.modularArmorConfig
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity

class SkeletonFriendlyEffect : TargetCancellationEffect(
    identifier("skeleton_friendly"),
    EntityCategory.SKELETON,
    modularArmorConfig.glitchArmor.costs::skeletonFriendly
) {

    override fun acceptTier(tier: DataModelTier) = tier.isMaxTier()
    override fun minimumTier(): DataModelTier = DataModelTier.SELF_AWARE

    override fun canApply(context: ModularEffectContext, payload: WrappedEffectTriggerPayload<LivingEntity>): Boolean {
        return payload.value.type == EntityType.SKELETON && super.canApply(context, payload)
    }
}
