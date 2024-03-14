package dev.nathanpb.dml.misc.lootfunction

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import dev.nathanpb.dml.baseConfig
import dev.nathanpb.dml.misc.PRISTINE_MATTER
import net.minecraft.item.ItemStack
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.function.ConditionalLootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.registry.Registries

class RandomPristineMatterLootFunction(conditions: Array<out LootCondition>): ConditionalLootFunction(conditions) {


    override fun process(stack: ItemStack, ctx: LootContext): ItemStack {
        val pristineMatterStack = ItemStack(
            Registries.ITEM.iterateEntries(PRISTINE_MATTER).iterator()
                .asSequence()
                .toList()
                .random()
        )
        val maxStackSize = baseConfig.misc.disruption.maxPristineMatterStackSize
        pristineMatterStack.count = ctx.random.nextInt(maxStackSize + 1).coerceIn(0, 64)

        return pristineMatterStack
    }

    override fun getType(): LootFunctionType = RANDOM_PRISTINE_MATTER

    class RandomPristineMatterLootFunctionSerializer: Serializer<RandomPristineMatterLootFunction>() {

        override fun fromJson(json: JsonObject, context: JsonDeserializationContext, conditions: Array<out LootCondition>): RandomPristineMatterLootFunction {
            return RandomPristineMatterLootFunction(conditions)
        }
    }
}