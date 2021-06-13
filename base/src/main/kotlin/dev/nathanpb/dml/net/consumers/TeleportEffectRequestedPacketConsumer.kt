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

package dev.nathanpb.dml.net.consumers

import dev.nathanpb.dml.event.TeleportEffectRequestedEvent
import dev.nathanpb.dml.utils.readVec3d
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

class TeleportEffectRequestedPacketConsumer : ServerPlayNetworking.PlayChannelHandler {

    override fun receive(
        server: MinecraftServer,
        player: ServerPlayerEntity,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
        val pos = buf.readVec3d()
        val rotation = buf.readVec3d()
        server.execute {
            if (
                pos.squaredDistanceTo(player.pos) <= 4*4
                && arrayOf(rotation.x, rotation.y, rotation.z).all { it in -128F..128F }
            ) {
                TeleportEffectRequestedEvent.invoker().invoke(player, pos, rotation)
            }
        }
    }
}
