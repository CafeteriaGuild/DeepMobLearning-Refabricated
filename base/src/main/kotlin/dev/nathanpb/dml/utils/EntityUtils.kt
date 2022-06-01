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

package dev.nathanpb.dml.utils

import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.server.world.ServerWorld
import java.util.*

fun EntityType<*>.simulateLootDroppedStacks(world: ServerWorld, player: PlayerEntity?, source: DamageSource): List<ItemStack> {
    val lootTable = world.server.lootManager?.getTable(lootTableId)
    val entity = create(world)
    val lootContext = LootContext.Builder(world).apply {
        random(player?.random)
        parameter(LootContextParameters.ORIGIN, entity?.pos)
        parameter(LootContextParameters.THIS_ENTITY, entity)
        parameter(LootContextParameters.DAMAGE_SOURCE, source)

        if (player != null) {
            parameter(LootContextParameters.KILLER_ENTITY, player)
            parameter(LootContextParameters.LAST_DAMAGE_PLAYER, player)
        }
    }.build(LootContextTypes.ENTITY)

    return lootTable?.generateLoot(lootContext) ?: emptyList()
}
