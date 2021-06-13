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

import dev.nathanpb.dml.armor.modular.core.ModularEffect
import dev.nathanpb.dml.armor.modular.core.ModularEffectContext
import dev.nathanpb.dml.armor.modular.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.LivingEntityEatEvent
import dev.nathanpb.dml.identifier
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import kotlin.math.ceil
import kotlin.random.Random

class UnrottenFleshEffect : ModularEffect<ModularEffectTriggerPayload> (
    identifier("unrotten_flesh"),
    EntityCategory.ZOMBIE,
    config.glitchArmor.costs::unrottenFlesh
) {

    companion object {
        val EFFECTS = listOf(
            StatusEffects.HASTE,
            StatusEffects.ABSORPTION,
            StatusEffects.FIRE_RESISTANCE,
            StatusEffects.REGENERATION,
            StatusEffects.RESISTANCE,
            StatusEffects.STRENGTH,
            StatusEffects.SPEED
        )
    }

    override fun registerEvents() {
        LivingEntityEatEvent.register { player, stack ->
            if (player is PlayerEntity && !player.world.isClient && stack.item == Items.ROTTEN_FLESH) {
                ModularEffectContext.from(player)
                    .forEach { context ->
                        attemptToApply(context, ModularEffectTriggerPayload.EMPTY) { _, _ ->
                            if (Random.nextFloat() <= sumLevelsOf(context.armor.stack)) {
                                context.player.addStatusEffect(StatusEffectInstance(
                                    EFFECTS.random(),
                                    context.tier.ordinal.inc() * 160,
                                    ceil(context.tier.ordinal.inc() / 2.5).toInt() -1
                                ))
                            }
                        }
                    }
            }
        }
    }

    override fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(id.toString(), (armor.tier().ordinal.inc() / 100.0) * 20, EntityAttributeModifier.Operation.MULTIPLY_BASE)
    }

    override fun acceptTier(tier: DataModelTier) = true
}
