package io.github.projectet.dmlSimulacrum.util;

import dev.nathanpb.dml.DeepMobLearningKt;
import dev.nathanpb.dml.data.DataModelDataKt;
import dev.nathanpb.dml.enums.DataModelTier;
import dev.nathanpb.dml.enums.EntityCategory;
import dev.nathanpb.dml.item.ItemDataModel;
import dev.nathanpb.dml.ModConfig;

import dev.nathanpb.dml.item.ItemPristineMatter;
import io.github.projectet.dmlSimulacrum.dmlSimulacrum;
import io.github.projectet.dmlSimulacrum.enums.MatterType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


public class DataModelUtil {
    public static void updateSimulationCount(ItemStack stack) {
        if(stack.getItem() instanceof ItemDataModel) {
            int i = getSimulationCount(stack) + 1;
            DataModelDataKt.getDataModel(stack).getTag().putInt("simulationCount", i);
        }
    }

    public static int getSimulationCount(ItemStack stack) {
        if(stack.getItem() instanceof ItemDataModel) {
            return DataModelDataKt.getDataModel(stack).getTag().getInt("simulationCount");
        }
        else {
            return 0;
        }
    }

    public static EntityCategory getEntityCategory(ItemStack stack) {
        if(stack.getItem() instanceof ItemDataModel) {
            return DataModelDataKt.getDataModel(stack).getCategory();
        }
        else {
            return null;
        }
    }

    public static int getTierCount(ItemStack stack) {
        if(stack.getItem() instanceof ItemDataModel) {
            return DataModelDataKt.getDataModel(stack).getDataAmount();
        }
        else {
            return 0;
        }
    }

    public static void updateTierCount(ItemStack stack) {
        if(stack.getItem() instanceof ItemDataModel) {
            DataModelDataKt.getDataModel(stack).setDataAmount(getTierCount(stack) + 1);
        }
    }

    public static int getEnergyCost(ItemStack stack) {
        return getEntityCategory(stack) != null ? dmlSimulacrum.energyCost.get(getEntityCategory(stack).toString()) : 0;
    }

    public static Text textType(ItemStack stack) {
        switch(Constants.dataModel.get(getEntityCategory(stack).toString()).getType()) {
            case OVERWORLD -> {
                return new LiteralText("Overworld").formatted(Formatting.GREEN);
            }
            case HELLISH -> {
                return new LiteralText("Hellish").formatted(Formatting.RED);
            }
            case EXTRATERRESTRIAL -> {
                return new LiteralText("Extraterrestrial").formatted(Formatting.LIGHT_PURPLE);
            }
            default -> {
                return new LiteralText("Invalid Item");
            }
        }
    }

    public static DataModelTier getTier(ItemStack stack) {
        if(stack.getItem() instanceof ItemDataModel) {
            return DataModelDataKt.getDataModel(stack).tier();
        }
        else {
            return null;
        }
    }

    public static int getTierRoof(ItemStack stack) {
        if (stack.getItem() instanceof ItemDataModel) {
            ModConfig config = DeepMobLearningKt.getConfig();
            switch (getTier(stack)) {
                case FAULTY -> {
                    return config.getDataModel().getBasicDataRequired();
                }
                case BASIC -> {
                    return config.getDataModel().getAdvancedDataRequired();
                }
                case ADVANCED -> {
                    return config.getDataModel().getSuperiorDataRequired();
                }
                case SUPERIOR -> {
                    return config.getDataModel().getSelfAwareDataRequired();
                }
            }
        }
        return 0;
    }

    public static Text textTier(ItemStack stack) {
        switch (getTier(stack)) {
            case FAULTY -> {
                return new LiteralText("Faulty").formatted(Formatting.GRAY);
            }
            case BASIC -> {
                return new LiteralText("Basic").formatted(Formatting.GREEN);
            }
            case ADVANCED -> {
                return new LiteralText("Advanced").formatted(Formatting.BLUE);
            }
            case SUPERIOR -> {
                return new LiteralText("Superior").formatted(Formatting.LIGHT_PURPLE);
            }
            case SELF_AWARE -> {
                return new LiteralText("Self Aware").formatted(Formatting.GOLD);
            }
            default -> {
                return new LiteralText("Invalid Item");
            }
        }
    }

    public static class DataModel2Matter {
        private final ItemPristineMatter pristine;
        private final MatterType type;

        DataModel2Matter(Item pristine, MatterType matter) {
            this.pristine = (ItemPristineMatter) pristine;
            this.type = matter;
        }

        public MatterType getType() {
            return type;
        }

        public ItemPristineMatter getPristine() {
            return pristine;
        }
    }
}
