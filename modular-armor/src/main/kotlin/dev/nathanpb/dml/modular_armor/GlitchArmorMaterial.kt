/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.modular_armor

import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.item.ITEM_GLITCH_INGOT
import dev.nathanpb.dml.utils.lerp
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.ArmorMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import kotlin.math.floor

class GlitchArmorMaterial : ArmorMaterial {

    companion object {
        val INSTANCE = GlitchArmorMaterial()
    }

    override fun getRepairIngredient(): Ingredient = Ingredient.ofItems(ITEM_GLITCH_INGOT)

    override fun getEquipSound(): SoundEvent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC

    override fun getDurability(type: ArmorItem.Type) = 1

    override fun getName() = "glitch"

    override fun getEnchantability() = 16

    override fun getKnockbackResistance() = 0F

    override fun getProtection(type: ArmorItem.Type) = 0

    override fun getToughness() = 0F

    fun getKnockbackResistance(tier: DataModelTier): Float {
        return (tier.ordinal.inc() / DataModelTier.values().size.toDouble()).lerp(1.0, 3.5).toFloat() / 10
    }

    fun getProtectionAmount(slot: EquipmentSlot, tier: DataModelTier): Int {
        val multiplier = when (slot) {
            EquipmentSlot.HEAD -> 0.9
            EquipmentSlot.CHEST -> 1.8
            EquipmentSlot.LEGS -> 1.4
            EquipmentSlot.FEET -> 0.8
            else -> 0.0
        }
        val result = (tier.ordinal / DataModelTier.values().size.toDouble()).lerp(4.0, 5.5)
        return floor(result * multiplier).toInt()
    }

    fun getToughness(tier: DataModelTier): Float {
        return tier.ordinal + 2F
    }

}
