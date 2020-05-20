package dev.nathanpb.dml.container

import dev.nathanpb.dml.identifier
import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.container.Container
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

val CONTAINER_DEEP_LEARNER = identifier("deep_learner")

private fun register(id: Identifier, factory: ContainerFactory<Container>) {
    ContainerProviderRegistry.INSTANCE.registerFactory(id, factory)
}

fun registerContainerTypes() {
    register(CONTAINER_DEEP_LEARNER, ContainerFactory { syncId, _, player, buff ->
        ContainerDeepLearner(syncId, player.inventory, Hand.valueOf(buff.readString()))
    })
}


