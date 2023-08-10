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

import dev.nathanpb.dml.config
import dev.nathanpb.dml.mixin.LootTableInvoker
import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.server.world.ServerWorld

fun EntityType<*>.simulateLootDroppedStacks(world: ServerWorld, player: PlayerEntity, source: DamageSource): List<ItemStack> {
    val lootTable = world.server.lootManager.getLootTable(lootTableId)
    val entity = create(world)

    val parameters = LootContextParameterSet.Builder(world).apply {
        add(LootContextParameters.ORIGIN, entity?.pos)
        add(LootContextParameters.THIS_ENTITY, entity)
        add(LootContextParameters.DAMAGE_SOURCE, source)

        add(LootContextParameters.KILLER_ENTITY, player)
        add(LootContextParameters.LAST_DAMAGE_PLAYER, player)
    }.build(LootContextTypes.ENTITY)

    val lootContext = LootContext.Builder(parameters).apply {
        random(player.lootTableSeed)
    }.build(null)

    val lootList = (lootTable as LootTableInvoker).invokeGenerateLoot(lootContext)
    lootList?.removeIf { stack: ItemStack -> !stack.isStackable && world.random.nextDouble() < config.lootFabricator.unstackableNullificationChance }

    return lootList ?: emptyList()
}