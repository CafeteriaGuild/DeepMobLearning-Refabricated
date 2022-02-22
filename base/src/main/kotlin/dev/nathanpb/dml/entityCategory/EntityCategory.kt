package dev.nathanpb.dml.entityCategory

import dev.nathanpb.dml.identifier
import net.minecraft.tag.EntityTypeTags
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier

data class EntityCategory(val id: Identifier) {
  val displayName = TranslatableText("mobcategory.${dev.nathanpb.dml.MOD_ID}.${id.path}")

  val entityTypes by lazy {
    EntityTypeTags.getTagGroup().getTag(id)!!.values()
  }

  override fun equals(other: Any?): Boolean {
    return (other as? EntityCategory)?.id?.equals(id) ?: super.equals(other)
  }

  companion object {
    private val register = EntityCategoryRegistry.INSTANCE::registerCategory

    val NETHER    = register(identifier("nether_mobs"))
    val SLIMY     = register(identifier("slimy_mobs"))
    val OVERWORLD = register(identifier("overworld_mobs"))
    val ZOMBIE    = register(identifier("zombie_mobs"))
    val SKELETON  = register(identifier("skeleton_mobs"))
    val END       = register(identifier("end_mobs"))
    val GHOST     = register(identifier("ghost_mobs"))
    val ILLAGER   = register(identifier("illager_mobs"))
    val OCEAN     = register(identifier("ocean_mobs"))
  }
}
