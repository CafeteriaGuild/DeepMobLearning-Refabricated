package dev.nathanpb.dml.enums;

import dev.nathanpb.dml.dmlSimulacrum;
import dev.nathanpb.dml.item.ItemMatter;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public enum MatterType {

    OVERWORLD(Registry.ITEM.get(dmlSimulacrum.id("overworld_matter"))),
    HELLISH(Registry.ITEM.get(dmlSimulacrum.id("hellish_matter"))),
    EXTRATERRESTRIAL(Registry.ITEM.get(dmlSimulacrum.id("extraterrestrial_matter")));

    private final ItemMatter matter;

    MatterType(Item matter) {
        this.matter = (ItemMatter) matter;
    }

    public ItemMatter getItem() {
        return matter;
    }
}
