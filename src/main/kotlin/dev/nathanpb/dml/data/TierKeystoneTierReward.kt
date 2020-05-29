/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.data

import dev.nathanpb.dml.utils.toTag
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.PacketByteBuf

data class TierKeystoneTierReward (val tier: DataModelTier, val stacks: List<ItemStack>) {

    companion object {
        const val WAVE_TAG_KEY = "wave"
        const val STACKS_TAG_KEY = "stacks"

        fun fromTag(tag: CompoundTag) = TierKeystoneTierReward(
            DataModelTier.values()[tag.getInt(WAVE_TAG_KEY)],
            tag.getList(STACKS_TAG_KEY, 10).mapNotNull {
                it as? CompoundTag
            }.map(ItemStack::fromTag)
        )
    }

    fun toTag() = CompoundTag().apply {
        putInt(WAVE_TAG_KEY, tier.ordinal)
        put(STACKS_TAG_KEY, ListTag().also { list ->
            list.addAll(stacks.map { it.toTag() })
        })
    }
}

fun PacketByteBuf.writeTierKeystoneReward(waveReward: TierKeystoneTierReward) = this.apply {
    writeCompoundTag(waveReward.toTag())
}

fun PacketByteBuf.readTierKeystoneReward() = TierKeystoneTierReward.fromTag(this.readCompoundTag() ?: CompoundTag())
