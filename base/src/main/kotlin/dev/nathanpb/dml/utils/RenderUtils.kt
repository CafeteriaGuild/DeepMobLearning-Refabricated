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

package dev.nathanpb.dml.utils

import com.mojang.blaze3d.systems.RenderSystem
import dev.nathanpb.dml.identifier
import io.github.cottonmc.cotton.gui.client.BackgroundPainter
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3f

class RenderUtils {

    companion object {
        const val TITLE_COLOR: Int = 0x04FCC4
        val BACKGROUND_PAINTER: BackgroundPainter = BackgroundPainter.createNinePatch(identifier("textures/gui/ui_panel.png"))
        val DARK_BACKGROUND_PAINTER: BackgroundPainter = BackgroundPainter.createNinePatch(Identifier(LibGuiCommon.MOD_ID, "textures/widget/panel_dark.png"))
    }
}

// not stolen from mojang, I promise
fun drawEntity(
    entity: LivingEntity,
    x: Int, y: Int, size: Int,
    tickDelta: Int,
    rotationZ: Float,
    rotationY: Float,
) {
    val matrixStack = RenderSystem.getModelViewStack()
    matrixStack.push()
    matrixStack.translate(x.toDouble(), y.toDouble(), 1050.0)
    matrixStack.scale(1.0f, 1.0f, -1.0f)
    RenderSystem.applyModelViewMatrix()
    val matrixStack2 = MatrixStack()
    matrixStack2.translate(0.0, 0.0, 1000.0)
    matrixStack2.scale(size.toFloat(), size.toFloat(), size.toFloat())
    val quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationZ)
    val quaternion2 = Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationY)
    quaternion.hamiltonProduct(quaternion2)
    matrixStack2.multiply(quaternion)


    DiffuseLighting.method_34742()
    val entityRenderDispatcher = MinecraftClient.getInstance().entityRenderDispatcher

    quaternion2.conjugate()
    entityRenderDispatcher.rotation = quaternion2

    entityRenderDispatcher.setRenderShadows(false)
    val immediate = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers
    RenderSystem.runAsFancy {
        entityRenderDispatcher.render(
            entity,
            0.0,
            0.0,
            0.0,
            0.0f,
            tickDelta.toFloat(),
            matrixStack2,
            immediate,
            15728880
        )
    }
    immediate.draw()
    entityRenderDispatcher.setRenderShadows(true)
    matrixStack.pop()
    RenderSystem.applyModelViewMatrix()
    DiffuseLighting.enableGuiDepthLighting()
}











































// vai toma no cu mano que tristeza
