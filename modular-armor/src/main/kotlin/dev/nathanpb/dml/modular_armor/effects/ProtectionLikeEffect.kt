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

import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.event.VanillaEvents
import dev.nathanpb.dml.modular_armor.core.ModularEffect
import dev.nathanpb.dml.modular_armor.core.ModularEffectContext
import dev.nathanpb.dml.modular_armor.core.ModularEffectTriggerPayload
import dev.nathanpb.dml.modular_armor.core.WrappedEffectTriggerPayload
import dev.nathanpb.dml.utils.takeOrNull
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.world.World

abstract class ProtectionLikeEffect(
    id: Identifier,
    category: EntityCategory,
    applyCost: ()->Long
) : ModularEffect<WrappedEffectTriggerPayload<VanillaEvents.LivingEntityDamageContext>>(id, category, applyCost) {

    override fun registerEvents() {
        VanillaEvents.LivingEntityDamageEvent.register { eventContext ->
            takeOrNull(eventContext.entity is PlayerEntity && !eventContext.entity.world.isClient) {
                val armorValues = ModularEffectContext.from(eventContext.entity as PlayerEntity).fold(0.0) { acc, effectContext ->
                    val result = attemptToApply(effectContext, ModularEffectTriggerPayload.wrap(eventContext)) { _, _ ->
                        sumLevelsOf(effectContext.armor.stack)
                    }

                    if (result.result == ActionResult.SUCCESS) {
                        acc + result.value
                    } else acc
                }
                eventContext.copy(damage = inflictDamage(eventContext, armorValues))
            }
        }
    }

    open fun inflictDamage(event: VanillaEvents.LivingEntityDamageContext, armorValues: Double): Float {
        return DamageUtil.getInflictedDamage(event.damage, armorValues.toFloat())
    }

    abstract fun protectsAgainst(world: World, source: DamageSource): Boolean

    override fun canApply(context: ModularEffectContext, payload: WrappedEffectTriggerPayload<VanillaEvents.LivingEntityDamageContext>): Boolean {
        return super.canApply(context, payload) && protectsAgainst(context.player.world, payload.value.source)
    }

    override fun getEnergyConsumptionType(): EffectInfo.EnergyConsumptionType = EffectInfo.EnergyConsumptionType.USE


}
