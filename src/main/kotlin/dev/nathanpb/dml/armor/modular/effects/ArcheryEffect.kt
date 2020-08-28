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
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.context.BowShotEvent
import dev.nathanpb.dml.event.context.CrossbowReloadedEvent
import dev.nathanpb.dml.identifier
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult

class ArcheryEffect : ModularEffect<ModularEffectTriggerPayload>(
    identifier("archery"),
    EntityCategory.SKELETON,
    config.glitchArmor.costs::archery
) {
    override fun registerEvents() {
        fun trigger(player: LivingEntity, stack: ItemStack) {
            if (player is PlayerEntity && !player.world.isClient) {
                ModularEffectContext.from(player)
                    .run(EffectStackOption.PRIORITIZE_GREATER.apply)
                    .any {
                        attemptToApply(it, ModularEffectTriggerPayload.EMPTY) == ActionResult.SUCCESS
                    }
            }
        }

        BowShotEvent.register(::trigger)
        CrossbowReloadedEvent.register(::trigger)
    }

    override fun acceptTier(tier: DataModelTier) = true

}
