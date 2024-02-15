package dev.nathanpb.dml.screen.handler.slot

import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon
import net.minecraft.client.gui.DrawContext
import net.minecraft.inventory.Inventory
import kotlin.math.min

open class WCyclingSlot(
    private val inventory: Inventory,
    private val index: Int,
    private var icons: List<TextureIcon>,
    big: Boolean = false
) : WItemSlot(inventory, index, 1, 1, big) {

    private var timer = 0
    private var iconIndex = 0
    private var hasItem = false


    override fun tick() {
        super.tick()
        updateTexture(icons)
    }

    override fun paint(context: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        if(backgroundPainter != null) {
            backgroundPainter!!.paintBackground(context, x, y, this)
        }


        if (icons.isNotEmpty()) {
            val bl = icons.size > 1 && timer >= 30
            val f = /*if(bl) computeAlpha(delta) else*/ 1.0f
            if (f < 1.0f) {
                val i = Math.floorMod(iconIndex - 1, icons.size)
                drawIcon(icons[i], 1.0f - f)
            }
            drawIcon(icons[iconIndex], f)
        }
    }

    fun updateTexture(icons: List<TextureIcon>) {
        if(this.icons != icons) {
            this.icons = icons
            iconIndex = 0
        }
        if(icons.isNotEmpty() && ++timer % 30 == 0) {
            iconIndex = (iconIndex + 1) % icons.size
        }
    }

    private fun drawIcon(icon: TextureIcon, alpha: Float) {
        icon.opacity = alpha
        setIcon(icon)
    }

    private fun computeAlpha(delta: Float): Float {
        val f = (timer % 30).toFloat() + delta
        return (min(f.toDouble(), 4.0) / 4.0f).toFloat()
    }

}