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

import dev.nathanpb.dml.event.VanillaEvents
import dev.nathanpb.dml.modular_armor.core.ModularEffectRegistry
import dev.nathanpb.dml.modular_armor.net.registerServerSidePackets
import dev.nathanpb.dml.modular_armor.screen.MatterCondenserScreenHandler
import dev.nathanpb.dml.modular_armor.screen.ModularArmorScreenHandler

@Suppress("unused")
fun init() {
    ItemModularGlitchArmor.register()
    BlockMatterCondenser.register()
    BlockEntityMatterCondenser.BLOCK_ENTITY_TYPE // force evaluate to register
    ModularEffectRegistry.registerDefaults()
    registerServerSidePackets()
    registerStatusEffects()

    MatterCondenserScreenHandler.INSTANCE // force evaluate to register
    ModularArmorScreenHandler.INSTANCE // force evaluate to register

    VanillaEvents.PlayerEntityTickEvent.register {
        it.flightBurnoutManager.tick()
    }
}
