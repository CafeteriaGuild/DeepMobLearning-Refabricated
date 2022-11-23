package dev.nathanpb.dml.simulacrum.util

import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.item.ItemPristineMatter
import dev.nathanpb.dml.item.*
import dev.nathanpb.dml.simulacrum.ENERGY_COST
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class DataModelUtil {

    companion object {

        val dataModel2MatterMap = hashMapOf(
            "NETHER" to DataModel2Matter(ITEM_PRISTINE_MATTER_NETHER, MatterType.HELLISH),
            "SLIMY" to DataModel2Matter(ITEM_PRISTINE_MATTER_SLIMY, MatterType.OVERWORLD),
            "OVERWORLD" to DataModel2Matter(ITEM_PRISTINE_MATTER_OVERWORLD, MatterType.OVERWORLD),
            "ZOMBIE" to DataModel2Matter(ITEM_PRISTINE_MATTER_ZOMBIE, MatterType.OVERWORLD),
            "SKELETON" to DataModel2Matter(ITEM_PRISTINE_MATTER_SKELETON, MatterType.OVERWORLD),
            "END" to DataModel2Matter(ITEM_PRISTINE_MATTER_END, MatterType.EXTRATERRESTRIAL),
            "GHOST" to DataModel2Matter(ITEM_PRISTINE_MATTER_GHOST, MatterType.HELLISH),
            "ILLAGER" to DataModel2Matter(ITEM_PRISTINE_MATTER_ILLAGER, MatterType.OVERWORLD),
            "OCEAN" to DataModel2Matter(ITEM_PRISTINE_MATTER_OCEAN, MatterType.OVERWORLD)
        )


        fun updateSimulationCount(stack: ItemStack) {
            if (stack.item is ItemDataModel) {
                val i = getSimulationCount(stack) + 1
                stack.dataModel.tag.putInt("simulationCount", i)
            }
        }

        fun getSimulationCount(stack: ItemStack): Int {
            return if(stack.item is ItemDataModel) stack.dataModel.tag.getInt("simulationCount") else 0
        }

        fun getEntityCategory(stack: ItemStack): EntityCategory? {
            return if(stack.item is ItemDataModel) stack.dataModel.category else null
        }

        fun getTierCount(stack: ItemStack): Int {
            return if(stack.item is ItemDataModel) stack.dataModel.dataAmount else 0
        }

        fun updateTierCount(stack: ItemStack) {
            if (stack.item is ItemDataModel) {
                stack.dataModel.dataAmount = getTierCount(stack) + 1
            }
        }

        fun getEnergyCost(stack: ItemStack): Int {
            return if (getEntityCategory(stack) != null) ENERGY_COST[getEntityCategory(stack).toString()]!! else 0
        }

        fun textType(stack: ItemStack): Text? { // TODO: Replace all 'dmlsimulacrum' id mentions to 'dml-refabricated' and update files accordingly
            return when(dataModel2MatterMap[getEntityCategory(stack).toString()]!!.type) {
                MatterType.OVERWORLD -> {
                    Text.translatable("modelType.dmlsimulacrum.overworld").formatted(Formatting.GREEN)
                }
                MatterType.HELLISH -> {
                    Text.translatable("modelType.dmlsimulacrum.hellish").formatted(Formatting.RED)
                }
                MatterType.EXTRATERRESTRIAL -> {
                    Text.translatable("modelType.dmlsimulacrum.extraterrestrial").formatted(Formatting.LIGHT_PURPLE)
                }
            }
        }

        fun getTier(stack: ItemStack): DataModelTier? {
            return if (stack.item is ItemDataModel) {
                stack.dataModel.tier()
            } else {
                null
            }
        }

        fun getTierRoof(stack: ItemStack): Int { // TODO: Move values to base's config
            if (stack.item is ItemDataModel) {
                val config = config
                when(getTier(stack)) {
                    DataModelTier.FAULTY -> {
                        return config.dataModel.basicDataRequired
                    }

                    DataModelTier.BASIC -> {
                        return config.dataModel.advancedDataRequired
                    }

                    DataModelTier.ADVANCED -> {
                        return config.dataModel.superiorDataRequired
                    }

                    DataModelTier.SUPERIOR -> {
                        return config.dataModel.selfAwareDataRequired
                    }

                    else -> {}
                }
            }
            return 0
        }

        fun textTier(stack: ItemStack): Text? { // TODO: Use base's translation keys
            return when(getTier(stack)) {
                DataModelTier.FAULTY -> {
                    Text.of("Faulty").copy().formatted(Formatting.GRAY)
                }
                DataModelTier.BASIC -> {
                    Text.of("Basic").copy().formatted(Formatting.GREEN)
                }
                DataModelTier.ADVANCED -> {
                    Text.of("Advanced").copy().formatted(Formatting.BLUE)
                }
                DataModelTier.SUPERIOR -> {
                    Text.of("Superior").copy().formatted(Formatting.LIGHT_PURPLE)
                }
                DataModelTier.SELF_AWARE -> {
                    Text.of("Self Aware").copy().formatted(Formatting.GOLD)
                }
                else -> {
                    Text.of("Invalid Item")
                }
            }
        }
    }

    class DataModel2Matter internal constructor(pristine: Item?, matter: MatterType) {
        val pristine: ItemPristineMatter?
        val type: MatterType

        init {
            this.pristine = pristine as ItemPristineMatter?
            type = matter
        }
    }
}