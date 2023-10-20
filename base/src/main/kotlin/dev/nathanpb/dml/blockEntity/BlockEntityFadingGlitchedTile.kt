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

package dev.nathanpb.dml.blockEntity

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos

class BlockEntityFadingGlitchedTile(
    pos: BlockPos, state: BlockState
) : BlockEntity(BLOCKENTITY_FADING_GLITCHED_TILE, pos, state) {


    var blockState: BlockState = Blocks.AIR.defaultState

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        blockState = NbtHelper.toBlockState(Registries.BLOCK.readOnlyWrapper, nbt.getCompound("BlockState"))
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.put("BlockState", NbtHelper.fromBlockState(blockState))
    }

}