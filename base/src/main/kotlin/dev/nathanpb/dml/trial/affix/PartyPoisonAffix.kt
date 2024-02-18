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
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.trial.Trial
import dev.nathanpb.dml.trial.TrialState
import dev.nathanpb.dml.trial.affix.core.TrialAffix
import dev.nathanpb.dml.utils.getPlayersByUUID
import dev.nathanpb.dml.utils.toVec3d
import net.minecraft.entity.projectile.thrown.PotionEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.potion.PotionUtil
import net.minecraft.potion.Potions
import net.minecraft.text.Text
import kotlin.random.Random

class PartyPoisonAffix : TrialAffix(identifier("party_poison")), TrialAffix.TickableAffix {

    override fun isEnabled() = baseConfig.trial.affixes.enablePartyPoison

    override fun tick(trial: Trial) {
        if (trial.state == TrialState.RUNNING && Random.nextFloat() < baseConfig.trial.affixes.partyPoisonChance) {

            (0..360).step(16).forEach { angle ->
                val potionEntity = PotionEntity(trial.world, trial.systemGlitch)
                trial.pos.toVec3d().apply {
                    potionEntity.setPos(x, y+1, z)
                }
                val stack = ItemStack(Items.SPLASH_POTION)
                PotionUtil.setPotion(stack, Potions.POISON)
                potionEntity.setItem(stack)
                potionEntity.setVelocity(trial.systemGlitch, -60F, angle.toFloat(), -20.0f, 0.5f, 1.0f)
                trial.world.spawnEntity(potionEntity)
            }

            if (Random.nextFloat() < .01F) {
                trial.world.getPlayersByUUID(trial.players).forEach {
                    it.sendMessage(Text.of("Hide your eyes, we're gonna shine tonight"), false)
                }
            }
        }
    }
}
