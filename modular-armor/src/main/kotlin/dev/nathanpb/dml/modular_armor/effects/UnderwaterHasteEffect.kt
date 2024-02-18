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

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.modular_armor.UNDERWATER_HASTE_EFFECT
import dev.nathanpb.dml.modular_armor.core.EffectStackOption
import dev.nathanpb.dml.modular_armor.core.ModularEffectContext
import dev.nathanpb.dml.modular_armor.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.modular_armor.modularArmorConfig
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.registry.tag.FluidTags
import net.minecraft.text.Text

class UnderwaterHasteEffect : StatusEffectLikeEffect(
    identifier("underwater_haste"),
    EntityCategory.OCEAN,
    modularArmorConfig.glitchArmor.costs::underwaterHaste,
    EffectStackOption.PRIORITIZE_GREATER
) {

    override val name = Text.translatable("effect.${MOD_ID}.underwater_haste")
    override val description = Text.translatable("effect.${MOD_ID}.underwater_haste.description")

    override fun createEffectInstance(context: ModularEffectContext): StatusEffectInstance {
        return StatusEffectInstance(UNDERWATER_HASTE_EFFECT, 16 * 20, context.tier.ordinal / 2, true, false)
    }

    override fun acceptTier(tier: DataModelTier) = true
    override fun minimumTier(): DataModelTier = DataModelTier.FAULTY
    // TODO should be per block?

    override fun canApply(context: ModularEffectContext, payload: ModularEffectTriggerPayload): Boolean {
        return super.canApply(context, payload) && context.player.isSubmergedIn(FluidTags.WATER)
    }

}
