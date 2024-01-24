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
import io.github.cottonmc.cotton.gui.client.NinePatchBackgroundPainter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.DustParticleEffect
import net.minecraft.text.Style
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis
import org.joml.Vector3f
import java.text.NumberFormat
import java.util.*


class RenderUtils {

    companion object {
        /* Textures */
        val DEFAULT_BACKGROUND_PAINTER: NinePatchBackgroundPainter by lazy {
            BackgroundPainter.createNinePatch(identifier("textures/gui/dml_background_painter.png"))
        }
        val DML_WIDGETS: Identifier = identifier("textures/gui/dml_widgets.png")

        val ARROW: Identifier = identifier("textures/gui/arrow.png")
        val ARROW_BACKGROUND: Identifier = identifier("textures/gui/arrow_background.png")

        val ENERGY_BAR: Identifier = identifier("textures/gui/energy_bar.png")
        val PRISTINE_ENERGY_BAR: Identifier = identifier("textures/gui/pristine_energy_bar.png")
        val ENERGY_BAR_BACKGROUND: Identifier = identifier("textures/gui/energy_bar_background.png")


        val CYAN_BAR: Identifier = identifier("textures/gui/cyan_bar.png")
        val BAR_BACKGROUND: Identifier = identifier("textures/gui/bar_background.png")

        /* Text Colors & Styles */
        const val TITLE_COLOR: Int = 0x04FCC4
        const val ALT_TITLE_COLOR: Int = 0x62D8FF
        val STYLE: Style = Style.EMPTY.withColor(TITLE_COLOR)
        val ALT_STYLE: Style = Style.EMPTY.withColor(ALT_TITLE_COLOR)
        val ENERGY_COLOR: Int = 0xFCD904
        val ENERGY_STYLE: Style = Style.EMPTY.withColor(ENERGY_COLOR)

        /* Particles */
        val GLITCH_PARTICLE: DustParticleEffect = DustParticleEffect(Vector3f(4F, 252F, 196F), 1F)
        val ALT_GLITCH_PARTICLE: DustParticleEffect = DustParticleEffect(Vector3f(98F, 216F, 255F), 1F)


        /*
         * Used to apply commas and periods to numbers according to the client's language
         *
         * eg.:
         * (en_us) = 10000 -> 10,000
         * (pt_br) = 10000 -> 10.000
         */
        fun formatAccordingToLanguage(): NumberFormat {
            val locale: Locale = Locale.forLanguageTag(
                MinecraftClient.getInstance().languageManager.language.replace(
                    "_",
                    "-"
                )
            )

            return NumberFormat.getNumberInstance(locale)
        }

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
    val quaternion = RotationAxis.POSITIVE_Z.rotationDegrees(rotationZ)
    val quaternion2 = RotationAxis.POSITIVE_Y.rotationDegrees(rotationY)
    quaternion.mul(quaternion2)
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
