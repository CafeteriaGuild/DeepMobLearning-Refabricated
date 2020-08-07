/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.nathanpb.dml.armor

import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.item.ITEM_GLITCH_INGOT
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

class GlitchArmorMaterial : ArmorMaterial {

    companion object {
        val INSTANCE = GlitchArmorMaterial()
    }

    override fun getRepairIngredient(): Ingredient = Ingredient.ofItems(ITEM_GLITCH_INGOT)

    override fun getEquipSound(): SoundEvent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC

    override fun getDurability(slot: EquipmentSlot?) = 1

    override fun getName() = "glitch"

    override fun getEnchantability() = 16

    override fun getKnockbackResistance() = 0F

    override fun getProtectionAmount(slot: EquipmentSlot?) = 0

    override fun getToughness() = 0F

    fun getKnockbackResistance(tier: DataModelTier): Float {
        return 0F
    }

    fun getProtectionAmount(slot: EquipmentSlot, tier: DataModelTier): Int {
        return 0
    }

    fun getToughness(tier: DataModelTier): Float {
        return 0F
    }

}
