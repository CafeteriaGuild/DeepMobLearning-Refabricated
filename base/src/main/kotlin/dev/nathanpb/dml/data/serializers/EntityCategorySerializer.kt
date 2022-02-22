package dev.nathanpb.dml.data.serializers

import dev.nathanpb.dml.entityCategory.EntityCategory
import dev.nathanpb.dml.entityCategory.EntityCategoryRegistry
import dev.nathanpb.ktdatatag.serializer.DataSerializer
import dev.nathanpb.ktdatatag.serializer.Serializers
import net.minecraft.nbt.NbtCompound

class EntityCategorySerializer : DataSerializer<EntityCategory> {
  private val inner = Serializers.IDENTIFIER

  override fun read(tag: NbtCompound, key: String): EntityCategory {
    val id = inner.read(tag, key)
    return if (isNullable())
      EntityCategoryRegistry.INSTANCE.get(id) as EntityCategory
    else
      EntityCategoryRegistry.INSTANCE.getOrThrow(id)
  }

  override fun write(tag: NbtCompound, key: String, data: EntityCategory) {
    inner.write(tag, key, data.id)
  }
}
