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
import dev.nathanpb.dml.event.context.PlayerStareEndermanEvent
import dev.nathanpb.dml.identifier
import net.minecraft.util.ActionResult

class EndermenProofVisionEffect : ModularEffect<ModularEffectTriggerPayload>(
    identifier("endermen_proof_vision"),
    EntityCategory.END,
    config.glitchArmor::enableEndermenProofVision,
    config.glitchArmor::endermenProofVisionCost
) {
    override fun registerEvents() {
        PlayerStareEndermanEvent.register { player ->
            if (!player.world.isClient) {
                val contexts = ModularEffectContext.from(player)
                    .run(EffectStackOption.PRIORITIZE_GREATER.apply)

                if (player.world.time % 20 == 0L) {
                    contexts.any { context ->
                        attemptToApply(context, ModularEffectTriggerPayload.EMPTY) { _, _ -> }
                            .result == ActionResult.SUCCESS
                    }
                }

                contexts.any { context -> canApply(context, ModularEffectTriggerPayload.EMPTY) }
                    .let { canApply ->
                        if (canApply) {
                            return@register ActionResult.FAIL
                        }
                    }
            }
            ActionResult.PASS
        }
    }

    override fun acceptTier(tier: DataModelTier) = true

}
