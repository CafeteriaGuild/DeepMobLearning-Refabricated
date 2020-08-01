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
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.Material
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView

class BlockCafeteria : HorizontalFacingBlock(
    FabricBlockSettings.of(Material.STONE)
        .hardness(1F)
        .resistance(.5F)
        .breakByHand(true)
        .nonOpaque()
) {
    init {
        defaultState = stateManager.defaultState
            .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun buildTooltip(
        stack: ItemStack?,
        world: BlockView?,
        tooltip: MutableList<Text>,
        options: TooltipContext?
    ) {
        tooltip += TranslatableText("tooltip.dml-refabricated.cafeteria.joinus").append(" https://discord.gg/G4PjhEf")
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return defaultState.with(FACING, ctx?.playerFacing?.opposite)
    }

    /*
    My complete fail trying obtain a cool model
    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return VoxelShapes.cuboid(0.125, 0.0, 0.005, 0.875, 0.75, 0.75)
    }*/
}
