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

package dev.nathanpb.dml.blockEntity

import dev.nathanpb.dml.block.BLOCK_LOOT_FABRICATOR
import dev.nathanpb.dml.block.BLOCK_MATTER_CONDENSER
import dev.nathanpb.dml.block.BLOCK_TRIAL_KEYSTONE
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

lateinit var BLOCKENTITY_TRIAL_KEYSTONE: BlockEntityType<BlockEntityTrialKeystone>
lateinit var BLOCKENTITY_LOOT_FABRICATOR: BlockEntityType<BlockEntityLootFabricator>
lateinit var BLOCKENTITY_MATTER_CONDENSER: BlockEntityType<BlockEntityMatterCondenser>

private fun <E: BlockEntity>mkSupplier(clazz: KClass<E>) = Supplier {
    clazz.primaryConstructor!!.call()
}

private fun <E: BlockEntity, B: Block>register(block: B, entityClass: KClass<E>) = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    Registry.BLOCK.getId(block),
    BlockEntityType.Builder.create(mkSupplier(entityClass), block).build(null)
)

fun registerBlockEntityTypes() {
    BLOCKENTITY_TRIAL_KEYSTONE = register(BLOCK_TRIAL_KEYSTONE, BlockEntityTrialKeystone::class)
    BLOCKENTITY_LOOT_FABRICATOR = register(BLOCK_LOOT_FABRICATOR, BlockEntityLootFabricator::class)
    BLOCKENTITY_MATTER_CONDENSER = register(BLOCK_MATTER_CONDENSER, BlockEntityMatterCondenser::class)
}
