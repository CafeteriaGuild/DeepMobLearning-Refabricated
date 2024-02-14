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
import dev.nathanpb.dml.modular_armor.core.*
import dev.nathanpb.dml.utils.firstInstanceOrNull
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult

class PiglinTruceEffect : ModularEffect<ModularEffectTriggerPayload>(
    identifier("piglin_truce"),
    EntityCategory.NETHER,
    config.glitchArmor.costs::piglinTruce
) {

    companion object {
        private val INSTANCE by lazy {
            ModularEffectRegistry.INSTANCE.all.firstInstanceOrNull<PiglinTruceEffect>()
        }

        fun trigger(player: PlayerEntity): ActionResult = INSTANCE?.run {
            if (!player.world.isClient) {
                val contexts = ModularEffectContext.from(player)
                    .run(EffectStackOption.PRIORITIZE_GREATER.apply)

                if (player.world.time % 20 == 0L) { // FIXME works but doesn't consume energy
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

    override fun registerEvents() { }

    override fun acceptTier(tier: DataModelTier) = tier.ordinal >= 2
    override fun minimumTier(): DataModelTier = DataModelTier.ADVANCED

}