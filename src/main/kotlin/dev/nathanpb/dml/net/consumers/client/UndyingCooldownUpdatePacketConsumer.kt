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

package dev.nathanpb.dml.net.consumers.client

import dev.nathanpb.dml.gui.hud.UndyingCooldownHud
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf

class UndyingCooldownUpdatePacketConsumer : PacketConsumer {
    override fun accept(context: PacketContext, buf: PacketByteBuf) {
        val cooldownTime = buf.readInt()
        val maxCooldownTime = buf.readInt()
        context.taskQueue.execute {
            UndyingCooldownHud.INSTANCE.cooldownTime = cooldownTime
            UndyingCooldownHud.INSTANCE.maxCooldownTime = maxCooldownTime
        }
    }

}
