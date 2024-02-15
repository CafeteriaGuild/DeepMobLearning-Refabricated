package dev.nathanpb.dml.screen.handler.widget

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.icon.Icon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

class CyclingTextureIcon(
    private val icons: List<Identifier>
): Icon {

    private var timer = 0
    private var iconIndex = 0

    @Environment(EnvType.CLIENT)
    @Override
    override fun paint(context: DrawContext, x: Int, y: Int, size: Int) {
        if(++timer % 45 == 0) {
            iconIndex = (iconIndex + 1) % icons.size
        }

        ScreenDrawing.texturedRect(context, x, y, size, size, icons[iconIndex], -0x1, 1F)
    }

}