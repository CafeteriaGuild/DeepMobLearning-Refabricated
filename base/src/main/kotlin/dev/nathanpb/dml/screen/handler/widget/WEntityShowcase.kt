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

import dev.nathanpb.dml.screen.handler.DeepLearnerScreenHandler
import dev.nathanpb.dml.utils.drawEntity
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.village.VillagerData
import net.minecraft.village.VillagerDataContainer
import net.minecraft.village.VillagerProfession
import net.minecraft.village.VillagerType
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class WEntityShowcase(
    private val root: SyncedGuiDescription
) : WWidget() {

    var entityTypes: List<EntityType<*>> = emptyList()

    private var tickCount = 0

    val entityType: EntityType<*>?
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

    override fun paint(ctx: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        super.paint(ctx, x, y, mouseX, mouseY)
        ctx.matrices.push()

        val entityType = entityType ?: return
        val world = MinecraftClient.getInstance().world ?: return
        val entity = entityType.create(world) as? LivingEntity ?: return

        if (entity is VillagerDataContainer)
            entity.villagerData = VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 0)

        val w = (this.width / 2)
        val h = (height / 1.25).roundToInt()

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
        ctx.matrices.pop()

        // only update at the exact tick the mob changes
        if(tickCount % 3 == 0) {
            (root as DeepLearnerScreenHandler).updateEntityInformation()
        }
    }

}
