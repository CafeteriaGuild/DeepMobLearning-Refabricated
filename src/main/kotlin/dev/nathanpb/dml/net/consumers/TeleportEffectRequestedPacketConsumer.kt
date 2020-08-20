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

import dev.nathanpb.dml.event.context.TeleportEffectRequestedEvent
import dev.nathanpb.dml.utils.readVec3d
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf

class TeleportEffectRequestedPacketConsumer : PacketConsumer {
    override fun accept(context: PacketContext, buf: PacketByteBuf) {
        val pos = buf.readVec3d()
        val rotation = buf.readVec3d()
        context.taskQueue.execute {
            if (
                pos.squaredDistanceTo(context.player.pos) <= 4*4
                && arrayOf(rotation.x, rotation.y, rotation.z).all { it in -128F..128F }
            ) {
                TeleportEffectRequestedEvent.invoker().invoke(context.player, pos, rotation)
            }
        }
    }
}
