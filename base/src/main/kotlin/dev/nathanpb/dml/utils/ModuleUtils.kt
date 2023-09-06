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

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.jetbrains.annotations.Nullable

const val CORE_ID = "dml-refabricated"
const val BASE_ID = "dml-refabricated-base"
const val MODULAR_ARMOR_ID = "dml-refabricated-modular-armor"
const val SIMULACRUM_ID = "dmlsimulacrum"


fun isModLoaded(modId: String): Boolean {
    return FabricLoader.getInstance().isModLoaded(modId)
}

@Nullable
fun getModContainer(modId: String): ModContainer {
    return FabricLoader.getInstance().getModContainer(modId).orElse(null)
}


@Nullable
fun getBlockFromRegistry(id: Identifier): Block? {
    return getFromRegistry(id, Registries.BLOCK) as Block?
}

@Nullable
fun getItemFromRegistry(id: Identifier): Item? {
    return getFromRegistry(id, Registries.ITEM) as Item?
}

@Nullable
fun getFromRegistry(id: Identifier, registry: Registry<*>): Any? {
    return registry.get(id)
}
