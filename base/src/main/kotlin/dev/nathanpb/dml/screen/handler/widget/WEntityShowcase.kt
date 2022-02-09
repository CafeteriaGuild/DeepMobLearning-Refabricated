/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.screen.handler.widget

import dev.nathanpb.dml.utils.drawEntity
import io.github.cottonmc.cotton.gui.client.BackgroundPainter
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon
import io.github.cottonmc.cotton.gui.widget.WWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class WEntityShowcase : WWidget() {

    var entityTypes: List<EntityType<*>> = emptyList()

    var tickCount = 0


    private val entityType: EntityType<*>?
        get() {
            return entityTypes.takeIf { entityTypes.isNotEmpty() }?.let { values ->
                values.toTypedArray()[(tickCount / 60) % values.size]
            }
        }

    override fun tick() {
        super.tick()
        tickCount++
    }

    override fun canResize() = true

    override fun paint(matrices: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        super.paint(matrices, x, y, mouseX, mouseY)
        matrices.push()

        BackgroundPainter.createNinePatch(Identifier( // TODO: Change this for actual texture
            LibGuiCommon.MOD_ID, "textures/widget/panel_dark.png"))
            .paintBackground(matrices, x, y, this)

        val entityType = entityType ?: return
        val world = MinecraftClient.getInstance().world ?: return
        val entity = entityType.create(world) as? LivingEntity ?: return

        val w = (this.width / 2)
        val h = (height / 1.5).roundToInt()

        val scaleFactor = min(w, h) * 1.5
        val entityScaleFactor = max(entity.width, entity.height)

        drawEntity(
            entity,
            x + w,
            y + h,
            (scaleFactor / entityScaleFactor).roundToInt(),
            0, 180F,
            (tickCount % 360F) * 2F + 150F
        )
        matrices.pop()
    }
}
