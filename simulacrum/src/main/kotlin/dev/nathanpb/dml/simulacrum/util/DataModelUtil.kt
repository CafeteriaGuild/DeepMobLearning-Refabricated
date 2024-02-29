package dev.nathanpb.dml.simulacrum.util

import dev.nathanpb.dml.baseConfig
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.enums.MatterType
import dev.nathanpb.dml.item.*
import dev.nathanpb.dml.simulacrum.simulacrumConfig
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class DataModelUtil {

    companion object {

        val dataModel2MatterMap = hashMapOf(
            EntityCategory.NETHER to DataModel2Matter(ITEM_PRISTINE_MATTER_NETHER, MatterType.HELLISH),
            EntityCategory.SLIMY to DataModel2Matter(ITEM_PRISTINE_MATTER_SLIMY, MatterType.OVERWORLD),
            EntityCategory.OVERWORLD to DataModel2Matter(ITEM_PRISTINE_MATTER_OVERWORLD, MatterType.OVERWORLD),
            EntityCategory.ZOMBIE to DataModel2Matter(ITEM_PRISTINE_MATTER_ZOMBIE, MatterType.OVERWORLD),
            EntityCategory.SKELETON to DataModel2Matter(ITEM_PRISTINE_MATTER_SKELETON, MatterType.OVERWORLD),
            EntityCategory.END to DataModel2Matter(ITEM_PRISTINE_MATTER_END, MatterType.EXTRATERRESTRIAL),
            EntityCategory.GHOST to DataModel2Matter(ITEM_PRISTINE_MATTER_GHOST, MatterType.HELLISH),
            EntityCategory.ILLAGER to DataModel2Matter(ITEM_PRISTINE_MATTER_ILLAGER, MatterType.OVERWORLD),
            EntityCategory.OCEAN to DataModel2Matter(ITEM_PRISTINE_MATTER_OCEAN, MatterType.OVERWORLD)
        )


        fun updateDataModel(stack: ItemStack) {
            val dataBonus = simulacrumConfig.simulationChamber.dataBonus
            if (stack.item !is ItemDataModel || dataBonus == 0) return

            stack.dataModel.dataAmount =
                (stack.dataModel.dataAmount + dataBonus).coerceIn(0, baseConfig.dataModel.selfAwareDataRequired)
            stack.dataModel.simulated = true
        }

        fun getEnergyCost(stack: ItemStack): Int {
            if (stack.item !is ItemDataModel) return 0
            val entityCategory = stack.dataModel.category ?: return 0
            return (entityCategory.energyValue.toFloat() * simulacrumConfig.simulationChamber.energyCostMultiplier).toInt()
        }

        fun getTierRoof(stack: ItemStack): Int {
            if (stack.item is ItemDataModel) {
                return when (stack.dataModel.tier()) {
                    DataModelTier.FAULTY -> baseConfig.dataModel.basicDataRequired
                    DataModelTier.BASIC -> baseConfig.dataModel.advancedDataRequired
                    DataModelTier.ADVANCED -> baseConfig.dataModel.superiorDataRequired
                    DataModelTier.SUPERIOR -> baseConfig.dataModel.selfAwareDataRequired
                    DataModelTier.SELF_AWARE -> 0
                }
            }
            return 0
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
}