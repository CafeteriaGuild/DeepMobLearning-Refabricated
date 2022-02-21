package io.github.projectet.dmlSimulacrum.config;

import io.github.projectet.dmlSimulacrum.dmlSimulacrum;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.lang.reflect.Field;

@me.shedaniel.autoconfig.annotation.Config(name = "dml-simulacrum")
public class Config implements ConfigData{

    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Category("default")
    @ConfigEntry.Gui.CollapsibleObject
    public MatterXP Matter_XP = new MatterXP();

    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Category("default")
    @ConfigEntry.Gui.CollapsibleObject
    public PristineChance Pristine_Chance = new PristineChance();

    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Category("default")
    @ConfigEntry.Gui.CollapsibleObject
    public EnergyCost Energy_Cost = new EnergyCost();

    @Override
    public void validatePostLoad() {
        MatterXP StaticMatter = new MatterXP();
        PristineChance StaticPristine = new PristineChance();
        EnergyCost StaticCost = new EnergyCost();

        for(Field x: Matter_XP.getClass().getFields()) {
            try {
                int fieldValue = x.getInt(this.Matter_XP);
                if(!dmlSimulacrum.inRange(fieldValue, 1, 999)) {
                    x.set(this.Matter_XP, StaticMatter.getClass().getField(x.getName()).get(StaticMatter));
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        for(Field x: Pristine_Chance.getClass().getFields()) {
            try {
                int fieldValue = x.getInt(this.Pristine_Chance);
                if(!dmlSimulacrum.inRange(fieldValue, 0, 100)) {
                    x.set(this.Pristine_Chance, StaticPristine.getClass().getField(x.getName()).get(StaticPristine));
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        for(Field x: Energy_Cost.getClass().getFields()) {
            try {
                int fieldValue = x.getInt(this.Energy_Cost);
                if(!dmlSimulacrum.inRange(fieldValue, 0, 6666)) {
                    x.set(this.Energy_Cost, StaticCost.getClass().getField(x.getName()).get(StaticCost));
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }


    public static class MatterXP {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 999)
        public int OverworldMatterXP = 10;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 999)
        public int HellishMatterXP = 14;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 999)
        public int ExtraMatterXP = 20;
    }

    public static class PristineChance{
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int BASIC = 5;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int ADVANCED = 11;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int SUPERIOR = 24;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int SELF_AWARE = 42;
    }

    public static class EnergyCost{
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6666)
        public int NETHER = 300;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6666)
        public int SLIMY = 160;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6666)
        public int OVERWORLD = 100;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6666)
        public int ZOMBIE = 300;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6666)
        public int SKELETON = 80;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6666)
        public int END = 512;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6666)
        public int GHOST = 372;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6666)
        public int ILLAGER = 412;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 6666)
        public int OCEAN = 160;
    }
}
