package dev.nathanpb.dml.modular_armor.screen

import dev.nathanpb.dml.mixin.WListPanelAccessor
import dev.nathanpb.dml.modular_armor.core.ModularEffect
import dev.nathanpb.dml.screen.handler.widget.WDarkScrollBar
import dev.nathanpb.dml.utils.RenderUtils.Companion.INNER_BACKGROUND_PAINTER
import io.github.cottonmc.cotton.gui.widget.WListPanel
import io.github.cottonmc.cotton.gui.widget.data.Axis
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.DrawContext
import java.util.function.BiConsumer
import java.util.function.Supplier


class WModuleListPanel(
    data: List<ModularEffect<*>>,
    supplier: Supplier<WModularEffectToggle>,
    configurator: BiConsumer<ModularEffect<*>, WModularEffectToggle>
) : WListPanel<ModularEffect<*>, WModularEffectToggle>(data, supplier, configurator) {

    init {
        if(FabricLoader.getInstance().environmentType == EnvType.CLIENT) { //addPainters is too late (idk why)
            backgroundPainter = INNER_BACKGROUND_PAINTER
        }
        scrollBar = WDarkScrollBar(Axis.VERTICAL)
        setListItemHeight(18)
    }


    override fun paint(context: DrawContext?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        backgroundPainter!!.paintBackground(context, x, y, this)

        if (scrollBar.value != (this as WListPanelAccessor).lastScroll) {
            layout()
            (this as WListPanelAccessor).lastScroll = scrollBar.value
        }
        for(child in children) {
            child.paint(context, x + child.x, y + child.y, mouseX - child.x, mouseY - child.y)
        }
    }

}