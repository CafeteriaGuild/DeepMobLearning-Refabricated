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

package dev.nathanpb.dml.modular_armor

import dev.nathanpb.dml.modular_armor.net.registerClientSidePackets
import dev.nathanpb.dml.modular_armor.screen.MatterCondenserScreenHandler
import dev.nathanpb.dml.modular_armor.screen.ModularArmorScreenHandler
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.gui.screen.ingame.HandledScreens

fun initClient() {
    registerKeybindings()
    registerClientSidePackets()

    HandledScreens.register(MatterCondenserScreenHandler.INSTANCE) { handler, inventory, title ->
        CottonInventoryScreen(handler, inventory.player, title)
    }
    HandledScreens.register(ModularArmorScreenHandler.INSTANCE) { handler, inventory, title ->
        CottonInventoryScreen(handler, inventory.player, title)
    }

    ItemPristineEnergyCube.ITEM_PRISTINE_ENERGY_CUBE.let {
        ColorProviderRegistry.ITEM.register({ stack, _ -> it.getScaledColor(stack) }, it)
    }
}
