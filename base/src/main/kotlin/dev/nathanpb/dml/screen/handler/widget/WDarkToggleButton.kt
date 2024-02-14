package dev.nathanpb.dml.screen.handler.widget

import io.github.cottonmc.cotton.gui.widget.WToggleButton

open class WDarkToggleButton: WToggleButton() {

    override fun shouldRenderInDarkMode(): Boolean = true
}