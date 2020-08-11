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

package dev.nathanpb.dml.screen

import dev.nathanpb.dml.screen.handler.HANDLER_LOOT_FABRICATOR
import dev.nathanpb.dml.screen.handler.HANDLER_MATTER_CONDENSER
import dev.nathanpb.dml.screen.handler.HANDLER_MODULAR_ARMOR
import dev.nathanpb.dml.screen.handler.ModularArmorScreenHandler
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry

fun registerScreens() {
    ScreenRegistry.register(HANDLER_LOOT_FABRICATOR) {
        lootFabricatorHandler, playerInventory, text -> LootFabricatorScreen(lootFabricatorHandler, playerInventory.player, text)
    }
    ScreenRegistry.register(HANDLER_MATTER_CONDENSER) {
        handler, playerInventory, text -> MatterCondenserScreen(handler, playerInventory.player, text)
    }
    ScreenRegistry.register(HANDLER_MODULAR_ARMOR) {
        handler, inventory, title -> CottonInventoryScreen<ModularArmorScreenHandler>(handler, inventory.player, title)
    }
}
