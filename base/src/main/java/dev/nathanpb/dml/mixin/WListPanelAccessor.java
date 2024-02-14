package dev.nathanpb.dml.mixin;

import io.github.cottonmc.cotton.gui.widget.WListPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WListPanel.class, remap = false)
public interface WListPanelAccessor {

    @Accessor
    int getLastScroll();

    @Accessor
    void setLastScroll(int lastScroll);

}