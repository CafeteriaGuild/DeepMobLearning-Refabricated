package dev.nathanpb.dml.entityCategory

import com.mojang.serialization.Lifecycle
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.mixin.RegistryInvoker
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

class EntityCategoryRegistry : SimpleRegistry<EntityCategory>(KEY, Lifecycle.experimental()) {

  companion object {
    val KEY = RegistryKey.ofRegistry<EntityCategory>(identifier("entity_category"))
    val INSTANCE: EntityCategoryRegistry = RegistryInvoker.invokeCreate(
      KEY,
      EntityCategoryRegistry(),
      EntityCategory.Companion::NETHER,
      Lifecycle.experimental()
    )
  }

  fun registerCategory(id: Identifier): EntityCategory {
    return Registry.register(this, id, EntityCategory(id))
  }

  fun getOrThrow(id: Identifier): EntityCategory {
    return get(id) ?: throw IllegalStateException("Missing key in $KEY: $id")
  }

}
