/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.block

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.entity.EntityContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class BlockTrialKeystone : Block(
    FabricBlockSettings.of(Material.STONE)
        .hardness(4F)
        .resistance(3000F)
) {
    override fun getOutlineShape(
        state: BlockState?,
        view: BlockView?,
        pos: BlockPos?,
        context: EntityContext?
    ): VoxelShape {
        return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
    }
}
