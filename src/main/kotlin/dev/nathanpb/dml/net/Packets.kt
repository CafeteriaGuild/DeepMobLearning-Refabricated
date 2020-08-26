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

package dev.nathanpb.dml.net

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.net.consumers.SoulVisionRequestedPacketConsumer
import dev.nathanpb.dml.net.consumers.TeleportEffectRequestedPacketConsumer
import dev.nathanpb.dml.net.consumers.client.FlightBurnoutManagerUpdatePacketConsumer
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.util.Identifier

val C2S_TELEPORT_EFFECT_REQUESTED = identifier("teleport_effect_requested")
val C2S_SOUL_VISION_REQUESTED = identifier("soul_vision_requested")

val S2C_FLIGHT_BURNOUT_MANAGER_UPDATE = identifier("flight_burnout_manager_update")

fun registerClientSidePackets() {
    hashMapOf<Identifier, PacketConsumer>(
        S2C_FLIGHT_BURNOUT_MANAGER_UPDATE to FlightBurnoutManagerUpdatePacketConsumer()
    ).forEach { (id, consumer) ->
        ClientSidePacketRegistry.INSTANCE.register(id, consumer)
    }
}

fun registerServerSidePackets() {
    hashMapOf(
        C2S_TELEPORT_EFFECT_REQUESTED to TeleportEffectRequestedPacketConsumer(),
        C2S_SOUL_VISION_REQUESTED to SoulVisionRequestedPacketConsumer()
    ).forEach { (id, consumer) ->
        ServerSidePacketRegistry.INSTANCE.register(id, consumer)
    }
}
