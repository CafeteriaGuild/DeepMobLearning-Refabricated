package dev.nathanpb.dml.screen.handler.widget

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.mixin.DrawContextAccessor
import io.github.cottonmc.cotton.gui.widget.WSprite
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class WInfoBubbleWidget(
    texture: Identifier,
    val text: List<Text>,
    var hidden: Boolean = false
): WSprite(texture) {

    companion object {
        val INFO_BUBBLE = identifier("textures/gui/info_bubble.png")
        val WARNING_BUBBLE = identifier("textures/gui/warning_bubble.png")
    }

    override fun paint(context: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        if(hidden) return
        super.paint(context, x, y, mouseX, mouseY)

        if(!isHovered) return
        context.drawTooltip(
            (context as DrawContextAccessor).client.textRenderer,
            text,
            x + mouseX, y + mouseY
        )
    }

}