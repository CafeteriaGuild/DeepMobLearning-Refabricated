package io.github.projectet.dmlSimulacrum.util;

import io.github.projectet.dmlSimulacrum.enums.MatterType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public interface Constants {
    //Simulation Chamber index slots.
    int DATA_MODEL_SLOT = 0;
    int INPUT_SLOT = 1;
    int OUTPUT_SLOT = 2;
    int PRISTINE_SLOT = 3;

    HashMap<String, DataModelUtil.DataModel2Matter> dataModel = new HashMap<>(Map.ofEntries(
            Map.entry("NETHER", new DataModelUtil.DataModel2Matter(Registry.ITEM.get(new Identifier("dml-refabricated", "pristine_matter_nether")), MatterType.HELLISH)),
            Map.entry("SLIMY", new DataModelUtil.DataModel2Matter(Registry.ITEM.get(new Identifier("dml-refabricated", "pristine_matter_slimy")), MatterType.OVERWORLD)),
            Map.entry("OVERWORLD", new DataModelUtil.DataModel2Matter(Registry.ITEM.get(new Identifier("dml-refabricated", "pristine_matter_overworld")), MatterType.OVERWORLD)),
            Map.entry("ZOMBIE", new DataModelUtil.DataModel2Matter(Registry.ITEM.get(new Identifier("dml-refabricated", "pristine_matter_zombie")), MatterType.OVERWORLD)),
            Map.entry("SKELETON", new DataModelUtil.DataModel2Matter(Registry.ITEM.get(new Identifier("dml-refabricated", "pristine_matter_skeleton")), MatterType.OVERWORLD)),
            Map.entry("END", new DataModelUtil.DataModel2Matter(Registry.ITEM.get(new Identifier("dml-refabricated", "pristine_matter_end")), MatterType.EXTRATERRESTRIAL)),
            Map.entry("GHOST", new DataModelUtil.DataModel2Matter(Registry.ITEM.get(new Identifier("dml-refabricated", "pristine_matter_ghost")), MatterType.HELLISH)),
            Map.entry("ILLAGER", new DataModelUtil.DataModel2Matter(Registry.ITEM.get(new Identifier("dml-refabricated", "pristine_matter_illager")), MatterType.OVERWORLD)),
            Map.entry("OCEAN", new DataModelUtil.DataModel2Matter(Registry.ITEM.get(new Identifier("dml-refabricated", "pristine_matter_ocean")), MatterType.OVERWORLD))
    ));
}
