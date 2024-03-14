package dev.nathanpb.dml.misc.lootfunction

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import dev.nathanpb.dml.baseConfig
import net.minecraft.item.ItemStack
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.function.ConditionalLootFunction
import net.minecraft.loot.function.LootFunctionType
import team.reborn.energy.api.base.SimpleEnergyItem

class RandomEnergyLootFunction(conditions: Array<out LootCondition>): ConditionalLootFunction(conditions) {


    override fun process(stack: ItemStack, ctx: LootContext): ItemStack {
        if(stack.item is SimpleEnergyItem) {
            val capacity = (stack.item as SimpleEnergyItem).getEnergyCapacity(stack)
            val percentage = ctx.random.nextFloat().coerceIn(0F, baseConfig.misc.disruption.maxEnergyOctahedronEnergyPercentage)
            stack.orCreateNbt.putLong(SimpleEnergyItem.ENERGY_KEY, (capacity * percentage).toLong())
        }
        return stack
    }

    override fun getType(): LootFunctionType = RANDOM_ENERGY

    class RandomEnergyLootFunctionSerializer: Serializer<RandomEnergyLootFunction>() {

        override fun fromJson(json: JsonObject, context: JsonDeserializationContext, conditions: Array<out LootCondition>): RandomEnergyLootFunction {
            return RandomEnergyLootFunction(conditions)
        }
    }
}