/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.compat.rei.widgets

import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand

class EntityDisplayWidget(
    private val entities: List<LivingEntity>,
    private val x: Int,
    private val y: Int,
    private val mouseX: Float,
    private val mouseY: Float,
    private val size: Int,
    private val stackInMainHand: ItemStack? = null,
    private val preRender: ((LivingEntity)->Unit) = {}
) : Drawable, Element {

    private var tickCount = 0

    override fun render(matrices: MatrixStack?, mX: Int, mY: Int, delta: Float) {
        tickCount++
        val entity = entities[(tickCount / 60) % entities.size]
        val initialStackMainHand = stackInMainHand?.let {
            entity.getStackInHand(Hand.MAIN_HAND).also {
                entity.setStackInHand(Hand.MAIN_HAND, stackInMainHand)
            }
        }

        preRender.invoke(entity)
        InventoryScreen.drawEntity(x, y, size, mouseX, mouseY, entity)

        if (stackInMainHand != null) {
            entity.setStackInHand(Hand.MAIN_HAND, initialStackMainHand)
        }
    }

}
