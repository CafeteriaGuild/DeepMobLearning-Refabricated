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

package dev.nathanpb.dml.modular_armor.net.consumers

import dev.nathanpb.dml.modular_armor.ItemModularGlitchArmor
import dev.nathanpb.dml.modular_armor.core.ModularEffectRegistry
import dev.nathanpb.dml.modular_armor.data.ModularArmorData
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand

class ModularEffectTogglePacketConsumer : ServerPlayNetworking.PlayChannelHandler {

    override fun receive(
        server: MinecraftServer,
        player: ServerPlayerEntity,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender?
    ) {
        val id = buf.readIdentifier()
        val flag = buf.readBoolean()
        val hand = Hand.values()[buf.readInt().coerceAtLeast(0).coerceAtMost(1)]
        server.execute {
            val stack = player.getStackInHand(hand)
            if (stack.item is ItemModularGlitchArmor) {
                if (ModularEffectRegistry.INSTANCE.fromId(id) != null) {
                    val data = ModularArmorData(stack)
                    if (flag) {
                        data.disabledEffects -= id
                    } else {
                        data.disabledEffects += id
                    }
                }
            }
        }
    }

}
