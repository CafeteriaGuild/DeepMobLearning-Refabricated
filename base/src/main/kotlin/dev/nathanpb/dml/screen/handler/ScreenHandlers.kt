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

package dev.nathanpb.dml.screen.handler

import dev.nathanpb.dml.identifier
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

val HANDLER_DEEP_LEARNER = registerScreenHandlerForItemStack(identifier("deep_learner"), ::DeepLearnerScreenHandler)
val HANDLER_DATA_SYNTHESIZER = registerScreenHandlerForBlockEntity(identifier("data_synthesizer"), ::DataSynthesizerScreenHandler)
val HANDLER_LOOT_FABRICATOR = registerScreenHandlerForBlockEntity(identifier("loot_fabricator"), ::LootFabricatorScreenHandler)

// TODO: Replace with non-deprecated version
fun <T: ScreenHandler>registerScreenHandlerForBlockEntity(
    id: Identifier,
    f: (Int, PlayerInventory, ScreenHandlerContext) -> T
): ExtendedScreenHandlerType<T> {
    return ScreenHandlerRegistry.registerExtended(id) { syncId, inventory, buf ->
        f(syncId, inventory, ScreenHandlerContext.create(inventory.player.world, buf.readBlockPos()))
    } as ExtendedScreenHandlerType<T>
}

fun <T: ScreenHandler>registerScreenHandlerForItemStack(
    id: Identifier,
    f: (Int, PlayerInventory, Hand) -> T
): ExtendedScreenHandlerType<T> {
    return ScreenHandlerRegistry.registerExtended(id) { syncId, inventory, buf ->
        f(syncId, inventory, Hand.values()[buf.readInt()])
    } as ExtendedScreenHandlerType<T>
}

fun registerScreenHandlers() {
    HANDLER_DEEP_LEARNER
    HANDLER_DATA_SYNTHESIZER
    HANDLER_LOOT_FABRICATOR
}
