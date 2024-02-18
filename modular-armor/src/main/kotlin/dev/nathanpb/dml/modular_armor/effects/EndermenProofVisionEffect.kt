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
import dev.nathanpb.dml.modular_armor.core.*
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import dev.nathanpb.dml.modular_armor.modularArmorConfig
import dev.nathanpb.dml.utils.firstInstanceOrNull
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult

class EndermenProofVisionEffect : ModularEffect<ModularEffectTriggerPayload>(
    identifier("endermen_proof_vision"),
    EntityCategory.END,
    modularArmorConfig.glitchArmor.costs::endermenProofVision
) {

    companion object {
        private val INSTANCE by lazy {
            ModularEffectRegistry.INSTANCE.all.firstInstanceOrNull<EndermenProofVisionEffect>()
        }

        fun trigger(player: PlayerEntity): ActionResult = INSTANCE?.run {
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
                            return ActionResult.FAIL
                        }
                    }
            }
            null
        } ?: ActionResult.PASS

    }

    override fun registerEvents() {

    }

    override fun acceptTier(tier: DataModelTier) = true
    override fun minimumTier(): DataModelTier = DataModelTier.FAULTY

    override fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(id.toString(), 1.0, EntityAttributeModifier.Operation.MULTIPLY_BASE)
    }

}
