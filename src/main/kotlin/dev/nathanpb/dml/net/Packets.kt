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
import dev.nathanpb.dml.net.consumers.TeleportEffectRequestedPacketConsumer
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.util.Identifier

val C2S_TELEPORT_EFFECT_REQUESTED = identifier("teleport_effect_requested")

fun registerClientSidePackets() {
    hashMapOf<Identifier, PacketConsumer>(
        // I'll be keeping this here to use when I need packets again
    ).forEach { (id, consumer) ->
        ClientSidePacketRegistry.INSTANCE.register(id, consumer)
    }
}

fun registerServerSidePackets() {
    hashMapOf<Identifier, PacketConsumer>(
        C2S_TELEPORT_EFFECT_REQUESTED to TeleportEffectRequestedPacketConsumer()
    ).forEach { (id, consumer) ->
        ServerSidePacketRegistry.INSTANCE.register(id, consumer)
    }
}
