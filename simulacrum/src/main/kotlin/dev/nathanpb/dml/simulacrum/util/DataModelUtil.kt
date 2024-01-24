package dev.nathanpb.dml.simulacrum.util

import dev.nathanpb.dml.config
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
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
            if(stack.item !is ItemDataModel) return
            val i = getSimulationCount(stack) + 1
            stack.dataModel.tag.putInt("simulationCount", i)
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

        fun updateDataModel(stack: ItemStack) {
            val dataBonus = config.simulationChamber.dataBonus
            if(stack.item !is ItemDataModel || dataBonus == 0) return

            stack.dataModel.dataAmount = (getTierCount(stack) + dataBonus).coerceIn(0, config.dataModel.selfAwareDataRequired)
            stack.dataModel.simulated = true
        }

        fun getEnergyCost(stack: ItemStack): Int {
            return if (getEntityCategory(stack) != null) ENERGY_COST[getEntityCategory(stack).toString()]!! else 0
        }

        fun textType(stack: ItemStack): Text? {
            return when(dataModel2MatterMap[getEntityCategory(stack).toString()]!!.type) {
                MatterType.OVERWORLD -> {
                    Text.translatable("modelType.dml-refabricated.overworld").formatted(Formatting.GREEN)
                }
                MatterType.HELLISH -> {
                    Text.translatable("modelType.dml-refabricated.hellish").formatted(Formatting.RED)
                }
                MatterType.EXTRATERRESTRIAL -> {
                    Text.translatable("modelType.dml-refabricated.extraterrestrial").formatted(Formatting.LIGHT_PURPLE)
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

        fun getTierRoof(stack: ItemStack): Int {
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

        fun textTier(stack: ItemStack): Text? {
            return when(getTier(stack)) {
                DataModelTier.FAULTY -> {
                    Text.translatable("tier.dml-refabricated.faulty")
                }
                DataModelTier.BASIC -> {
                    Text.translatable("tier.dml-refabricated.basic")
                }
                DataModelTier.ADVANCED -> {
                    Text.translatable("tier.dml-refabricated.advanced")
                }
                DataModelTier.SUPERIOR -> {
                    Text.translatable("tier.dml-refabricated.superior")
                }
                DataModelTier.SELF_AWARE -> {
                    Text.translatable("tier.dml-refabricated.self_aware")
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