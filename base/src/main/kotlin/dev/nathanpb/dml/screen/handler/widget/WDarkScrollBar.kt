package dev.nathanpb.dml.screen.handler.widget

import io.github.cottonmc.cotton.gui.widget.WScrollBar
import io.github.cottonmc.cotton.gui.widget.data.Axis

class WDarkScrollBar(axis: Axis): WScrollBar(axis) {

    override fun shouldRenderInDarkMode(): Boolean = true
}