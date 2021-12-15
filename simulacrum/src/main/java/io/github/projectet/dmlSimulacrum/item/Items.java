package io.github.projectet.dmlSimulacrum.item;

import dev.nathanpb.dml.item.ItemsKt;
import io.github.projectet.dmlSimulacrum.dmlSimulacrum;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class Items {

    public static void Register() {
        Registry.register(Registry.ITEM, dmlSimulacrum.id("polymer_clay"), POLYMER_CLAY);
        Registry.register(Registry.ITEM, dmlSimulacrum.id("overworld_matter"), OVERWORLD_MATTER);
        Registry.register(Registry.ITEM, dmlSimulacrum.id("hellish_matter"), HELLISH_MATTER);
        Registry.register(Registry.ITEM, dmlSimulacrum.id("extraterrestrial_matter"), EXTRATERRESTRIAL_MATTER);
    }

    public static final Item POLYMER_CLAY = new ItemPolymerClay(new FabricItemSettings().maxCount(64).group(ItemsKt.getITEM_GROUP()));
    public static final Item OVERWORLD_MATTER = new ItemMatter(new FabricItemSettings().maxCount(64).group(ItemsKt.getITEM_GROUP()), dmlSimulacrum.config.Matter_XP.OverworldMatterXP);
    public static final Item HELLISH_MATTER = new ItemMatter(new FabricItemSettings().maxCount(64).group(ItemsKt.getITEM_GROUP()), dmlSimulacrum.config.Matter_XP.HellishMatterXP);
    public static final Item EXTRATERRESTRIAL_MATTER = new ItemMatter(new FabricItemSettings().maxCount(64).group(ItemsKt.getITEM_GROUP()), dmlSimulacrum.config.Matter_XP.ExtraMatterXP);
}
