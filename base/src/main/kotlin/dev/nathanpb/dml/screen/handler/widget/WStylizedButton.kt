package dev.nathanpb.dml.screen.handler.widget

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.WButton
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier

open class WStylizedButton private constructor(
    val texture: Identifier,
    val overlayTextures: List<Identifier>?, // textures must be 20x20
    val textLabel: Text?
) : WButton(textLabel) {


    constructor(texture: Identifier, textLabel: Text) : this(texture, null, textLabel)

    constructor(texture: Identifier, overlayTextures: List<Identifier>) : this(texture, overlayTextures, null)

    var overlayTextureIndex = 0

    /*
     * PORTING NOTICE: This is, at its core, a copy of WButton's paint method.
     * Make sure to check it if anything breaks between versions!
     */
    @Environment(EnvType.CLIENT)
    override fun paint(ctx: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        val hovered = mouseX >= 0 && mouseY >= 0 && mouseX < getWidth() && mouseY < getHeight()
        var state = 1 // 1 = regular, 2 = hovered and 0 = disabled.
        if (!isEnabled) {
            state = 0
        } else if (hovered || isFocused) {
            state = 2
        }
        val px = 1 / 256f
        val buttonLeft = 0 * px
        val buttonTop = (46 + state * 20) * px
        var halfWidth = getWidth() / 2
        if (halfWidth > 198) halfWidth = 198
        val buttonWidth = halfWidth * px
        val buttonHeight = 20 * px
        val buttonEndLeft = (200 - getWidth() / 2) * px
        ScreenDrawing.texturedRect(
            ctx,
            x,
            y,
            getWidth() / 2,
            20,
            texture,
            buttonLeft,
            buttonTop,
            buttonLeft + buttonWidth,
            buttonTop + buttonHeight,
            -0x1
        )
        ScreenDrawing.texturedRect(
            ctx,
            x + getWidth() / 2,
            y,
            getWidth() / 2,
            20,
            texture,
            buttonEndLeft,
            buttonTop,
            200 * px,
            buttonTop + buttonHeight,
            -0x1
        )

        var color = 0xE0E0E0
        if (!isEnabled) {
            color = 0xA0A0A0
        }

        if(textLabel != null) {
            ScreenDrawing.drawStringWithShadow(
                ctx,
                textLabel.asOrderedText(),
                alignment,
                x,
                y + (20 - 8) / 2,
                width,
                color
            )
        } else { // Overlay Textures
            ScreenDrawing.texturedRect(
                ctx,
                x,
                y,
                20,
                20,
                overlayTextures!![overlayTextureIndex],
                0F,
                0F,
                1F,
                1F,
                0xFF_FFFFFF.toInt()
            )
        }
    }
}