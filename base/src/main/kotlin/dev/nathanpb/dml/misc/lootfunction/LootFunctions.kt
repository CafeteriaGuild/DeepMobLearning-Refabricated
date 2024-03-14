package dev.nathanpb.dml.misc.lootfunction

import dev.nathanpb.dml.identifier
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.JsonSerializer


val RANDOM_ENERGY: LootFunctionType = registerLootFunction("random_energy", RandomEnergyLootFunction.RandomEnergyLootFunctionSerializer())
val RANDOM_DATA_MODEL: LootFunctionType = registerLootFunction("random_data_model", RandomDataModelLootFunction.RandomDataModelLootFunctionSerializer())
val RANDOM_PRISTINE_MATTER: LootFunctionType = registerLootFunction("random_pristine_matter", RandomPristineMatterLootFunction.RandomPristineMatterLootFunctionSerializer())


private fun registerLootFunction(id: String, serializer: JsonSerializer<out LootFunction>): LootFunctionType {
    return Registry.register(Registries.LOOT_FUNCTION_TYPE, identifier(id), LootFunctionType(serializer))
}

fun registerLootFunctions() {
    RANDOM_ENERGY
    RANDOM_DATA_MODEL
    RANDOM_PRISTINE_MATTER
}