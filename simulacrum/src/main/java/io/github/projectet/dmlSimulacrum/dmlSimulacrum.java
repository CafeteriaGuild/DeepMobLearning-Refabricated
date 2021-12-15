package io.github.projectet.dmlSimulacrum;

import dev.nathanpb.dml.DeepMobLearningKt;
import dev.nathanpb.dml.item.ItemsKt;
import io.github.projectet.dmlSimulacrum.block.SimulationChamber;
import io.github.projectet.dmlSimulacrum.block.entity.SimulationChamberEntity;
import io.github.projectet.dmlSimulacrum.config.Config;
import io.github.projectet.dmlSimulacrum.item.Items;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import team.reborn.energy.api.EnergyStorage;

import java.lang.reflect.Field;
import java.util.HashMap;

public class dmlSimulacrum implements ModInitializer {

    public static final Block SIMULATION_CHAMBER = new SimulationChamber(FabricBlockSettings.of(Material.STONE).hardness(4f).resistance(3000f));
    public static BlockEntityType<SimulationChamberEntity> SIMULATION_CHAMBER_ENTITY;

    public final static String MOD_ID = "dmlsimulacrum";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static Config config;
    public static HashMap<String, Integer> pristineChance = new HashMap<>();
    public static HashMap<String, Integer> energyCost = new HashMap<>();

    @Override
    public void onInitialize() {
        AutoConfig.register(Config.class, GsonConfigSerializer::new);
        try {
            initMaps();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Registry.register(Registry.BLOCK, id("simulation_chamber"), SIMULATION_CHAMBER);
        Registry.register(Registry.ITEM, id("simulation_chamber"), new BlockItem(SIMULATION_CHAMBER, new Item.Settings().group(ItemsKt.getITEM_GROUP())));

        SIMULATION_CHAMBER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("simulation_chamber_entity"), FabricBlockEntityTypeBuilder.create(SimulationChamberEntity::new, SIMULATION_CHAMBER).build());
        EnergyStorage.SIDED.registerForBlockEntity(((blockEntity, direction) -> blockEntity.energyStorage), SIMULATION_CHAMBER_ENTITY);
        Items.Register();
    }

    public void initMaps() throws IllegalAccessException {
        config = AutoConfig.getConfigHolder(Config.class).getConfig();
        for (Field x: config.Pristine_Chance.getClass().getFields()) {
            pristineChance.put(x.getName(), x.getInt(config.Pristine_Chance));
        }
        for (Field x: config.Energy_Cost.getClass().getFields()) {
            energyCost.put(x.getName(), x.getInt(config.Energy_Cost));
        }
    }

    public static boolean inRange(int input, int min, int max) {
        return ((input >= min) && (input <= max));
    }

    public static int ensureRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }
}
