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

package dev.nathanpb.dml.modular_armor.effects

import dev.nathanpb.dml.config
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.identifier
import net.minecraft.entity.damage.DamageSource
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.text.Text
import net.minecraft.world.World

class FireProtectionEffect : ProtectionLikeEffect(
    identifier("fire_protection"),
    EntityCategory.NETHER,
    config.glitchArmor.costs::fireProtection
) {

    override val name = Text.translatable("enchantment.minecraft.fire_protection")
    override val description = Text.translatable("enchantment.minecraft.fire_protection.module_description")


    override fun protectsAgainst(world: World, source: DamageSource) = source.isIn(DamageTypeTags.IS_FIRE)

    override fun acceptTier(tier: DataModelTier) = !tier.isMaxTier()
    override fun minimumTier(): DataModelTier = DataModelTier.FAULTY

}
