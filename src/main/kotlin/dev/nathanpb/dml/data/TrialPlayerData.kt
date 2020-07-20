/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.data

import io.netty.buffer.Unpooled
import net.minecraft.network.PacketByteBuf

data class TrialPlayerData (
    val participants: Int,
    val currentWave: Int,
    val maxWaves: Int
) {
    fun toPacketByteBuff(buff: PacketByteBuf = PacketByteBuf(Unpooled.buffer())) = buff.writeTrialPlayerData(this)
}

fun PacketByteBuf.writeTrialPlayerData(data: TrialPlayerData) = this.apply {
    writeInt(data.participants)
    writeInt(data.currentWave)
    writeInt(data.maxWaves)
}

fun PacketByteBuf.readTrialPlayerData() = TrialPlayerData (
    readInt(), readInt(), readInt()
)
