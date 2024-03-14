package dev.nathanpb.dml.misc.lootfunction

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import dev.nathanpb.dml.baseConfig
import dev.nathanpb.dml.data.dataModel
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.misc.ATTUNED_DATA_MODELS
import net.minecraft.item.ItemStack
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.function.ConditionalLootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.registry.Registries

class RandomDataModelLootFunction(conditions: Array<out LootCondition>): ConditionalLootFunction(conditions) {


    override fun process(stack: ItemStack, ctx: LootContext): ItemStack {
        val dataModelStack = ItemStack(
            Registries.ITEM.iterateEntries(ATTUNED_DATA_MODELS).iterator()
                .asSequence()
                .toList()
                .random()
        )

        if(dataModelStack.item is ItemDataModel) {
            val maxData = baseConfig.misc.disruption.maxDataModelData
            dataModelStack.dataModel.dataAmount = ctx.random.nextInt(maxData + 1).coerceIn(0, baseConfig.dataModel.selfAwareDataRequired)
        }

        return dataModelStack
    }

    override fun getType(): LootFunctionType = RANDOM_DATA_MODEL

    class RandomDataModelLootFunctionSerializer: Serializer<RandomDataModelLootFunction>() {

        override fun fromJson(json: JsonObject, context: JsonDeserializationContext, conditions: Array<out LootCondition>): RandomDataModelLootFunction {
            return RandomDataModelLootFunction(conditions)
        }
    }
}