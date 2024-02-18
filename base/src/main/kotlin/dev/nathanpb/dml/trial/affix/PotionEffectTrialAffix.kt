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

package dev.nathanpb.dml.trial.affix

import dev.nathanpb.dml.baseConfig
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.trial.Trial
import dev.nathanpb.dml.trial.affix.core.TrialAffix
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.Identifier
import kotlin.random.Random

abstract class PotionEffectTrialAffix(
    id: Identifier,
    @Suppress("private") vararg val effects: StatusEffect
): TrialAffix(id), TrialAffix.WaveSpawnedListener  {

    override fun onWaveSpawn(trial: Trial, waveEntities: List<LivingEntity>) {
        val chanceOfApplying = (trial.recipe.tier.ordinal.inc().toFloat() / DataModelTier.values().size)
        waveEntities.filter {
            Random.nextFloat() <= chanceOfApplying
        }.forEach { entity ->
            effects.map { effect ->
                StatusEffectInstance(effect, (baseConfig.trial.maxTime - trial.tickCount).coerceAtLeast(0))
            }.forEach {
                entity.addStatusEffect(it)
            }
        }
    }

}

class MobStrengthTrialAffix : PotionEffectTrialAffix(identifier("mob_strength"), StatusEffects.STRENGTH) {
    override fun isEnabled() = baseConfig.trial.affixes.enableMobStrength
}

class MobSpeedTrialAffix : PotionEffectTrialAffix(identifier("mob_speed"), StatusEffects.SPEED) {
    override fun isEnabled() = baseConfig.trial.affixes.enableMobSpeed
}

class MobResistanceTrialAffix : PotionEffectTrialAffix(identifier("mob_resistance"), StatusEffects.RESISTANCE) {
    override fun isEnabled() = baseConfig.trial.affixes.enableMobResistance
}
