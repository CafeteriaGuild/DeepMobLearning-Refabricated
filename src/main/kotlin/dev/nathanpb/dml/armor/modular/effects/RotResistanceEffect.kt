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

import dev.nathanpb.dml.armor.modular.core.EffectStackOption
import dev.nathanpb.dml.armor.modular.core.ModularEffect
import dev.nathanpb.dml.armor.modular.core.ModularEffectContext
import dev.nathanpb.dml.armor.modular.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.context.FoodStatusEffectsCallback
import dev.nathanpb.dml.identifier
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import kotlin.random.Random

class RotResistanceEffect : ModularEffect<ModularEffectTriggerPayload>(
    identifier("rot_resistance"),
    EntityCategory.ZOMBIE,
    config.glitchArmor.costs::rotResistance
) {

    override fun registerEvents() {
        FoodStatusEffectsCallback.register { player, stack, effects ->
            if (player is PlayerEntity && !player.world.isClient && stack.item == Items.ROTTEN_FLESH) {
                ModularEffectContext.from(player)
                    .run(EffectStackOption.PRIORITIZE_GREATER.apply)
                    .firstOrNull { context ->
                        attemptToApply(context, ModularEffectTriggerPayload.EMPTY) == ActionResult.SUCCESS
                    }?.let { context ->
                        if (Random.nextFloat() <= sumLevelsOf(context.armor.stack)) {
                            return@register effects.filter { pair ->
                                pair.first.effectType != StatusEffects.HUNGER
                            }
                        }
                    }
            }

            return@register effects
        }
    }

    override fun acceptTier(tier: DataModelTier) = true

    override fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        val value = if (armor.tier() == DataModelTier.FAULTY) 0.5 else 1.0
        return EntityAttributeModifier(id.toString(), value, EntityAttributeModifier.Operation.MULTIPLY_BASE)
    }

}
