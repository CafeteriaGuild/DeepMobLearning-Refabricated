package dev.nathanpb.dml.mixin;

import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(SmithingTemplateItem.class)
public interface SmithingTemplateItemAccessor {

    @Invoker("getArmorTrimEmptyBaseSlotTextures")
    static List<Identifier> dml_getArmorTrimEmptyBaseSlotTextures() {
        throw new AssertionError();
    }

    @Invoker("getNetheriteUpgradeEmptyAdditionsSlotTextures")
    static List<Identifier> dml_getNetheriteUpgradeEmptyAdditionsSlotTextures() {
        throw new AssertionError();
    }
}
