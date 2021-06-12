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

package dev.nathanpb.dml.gui.screen.handler

import dev.nathanpb.dml.identifier
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

val HANDLER_LOOT_FABRICATOR = registerForBlockEntity(identifier("loot_fabricator"), ::LootFabricatorHandler)
val HANDLER_MATTER_CONDENSER = registerForBlockEntity(identifier("matter_condenser"), ::MatterCondenserHandler)
val HANDLER_MODULAR_ARMOR = registerForItemStack(identifier("modular_armor"), ::ModularArmorScreenHandler)
val HANDLER_DEEP_LEARNER = registerForItemStack(identifier("deep_learner"), ::DeepLearnerScreenHandler)

private fun <T: ScreenHandler>registerForBlockEntity(
    id: Identifier,
    f: (Int, PlayerInventory, ScreenHandlerContext) -> T
): ExtendedScreenHandlerType<T> {
    return ScreenHandlerRegistry.registerExtended(id) { syncId, inventory, buf ->
        f(syncId, inventory, ScreenHandlerContext.create(inventory.player.world, buf.readBlockPos()))
    } as ExtendedScreenHandlerType<T>
}

private fun <T: ScreenHandler>registerForItemStack(
    id: Identifier,
    f: (Int, PlayerInventory, Hand) -> T
): ExtendedScreenHandlerType<T> {
    return ScreenHandlerRegistry.registerExtended(id) { syncId, inventory, buf ->
        f(syncId, inventory, Hand.values()[buf.readInt()])
    } as ExtendedScreenHandlerType<T>
}

fun registerScreenHandlers() {
    HANDLER_LOOT_FABRICATOR
    HANDLER_MATTER_CONDENSER
    HANDLER_MODULAR_ARMOR
    HANDLER_DEEP_LEARNER
}
