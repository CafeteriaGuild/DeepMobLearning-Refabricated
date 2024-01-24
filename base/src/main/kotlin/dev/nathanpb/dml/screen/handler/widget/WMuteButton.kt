package dev.nathanpb.dml.screen.handler.widget

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.utils.RenderUtils.Companion.DML_WIDGETS
import io.github.cottonmc.cotton.gui.widget.data.InputResult

abstract class WMuteButton(
    defaultIndex: Int
) : WStylizedButton(
    DML_WIDGETS,
    listOf(
        identifier("textures/gui/sound_on_icon.png"),
        identifier("textures/gui/sound_off_icon.png")
    )
) {

    init {
        overlayTextureIndex = defaultIndex.coerceIn(0, 1)
    }

    abstract fun toggleMute()

    override fun onClick(x: Int, y: Int, button: Int): InputResult {
        toggleMute()
        overlayTextureIndex = if(overlayTextureIndex == 0) 1 else 0
        return super.onClick(x, y, button)
    }

}