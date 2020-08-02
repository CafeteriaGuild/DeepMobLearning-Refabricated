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

package dev.nathanpb.dml.listener

import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.event.LivingEntityDieCallback
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.item.ItemDeepLearner
import dev.nathanpb.dml.item.deepLearnerInventory
import dev.nathanpb.dml.utils.hotbar
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity

class DataCollectListener : LivingEntityDieCallback {
    override fun onDeath(entity: LivingEntity, damageSource: DamageSource?) {
        if (entity.world?.isClient == false) {
            (damageSource?.attacker as? PlayerEntity)?.let { player ->
                player.inventory.hotbar().filter {
                    it.item is ItemDeepLearner
                }.map {
                    it.deepLearnerInventory.filter { dlStack ->
                        dlStack.item is ItemDataModel
                    }.map { dlStack ->
                        dlStack.dataModel
                    }
                }.flatten().firstOrNull {
                    it.category?.tag?.contains(entity.type) ?: false && !it.tier().isMaxTier()
                }?.let {
                    it.dataAmount += config.dataCollection.baseDataGainPerKill
                }
            }
        }
    }
}
