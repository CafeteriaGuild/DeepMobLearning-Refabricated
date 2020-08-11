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

package dev.nathanpb.dml.armor.modular.core

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.data.ModularArmorData
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import net.minecraft.entity.attribute.ClampedEntityAttribute
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.jetbrains.annotations.ApiStatus

abstract class ModularEffect(
    val id: Identifier,
    val category: EntityCategory,
    val isEnabled: ()->Boolean,
    val applyCost: ()->Int
) {

    val name = if (id.namespace == MOD_ID) {
        TranslatableText("modulareffect.${MOD_ID}.${id.path}")
    } else {
        TranslatableText("modulareffect.${MOD_ID}.${id.namespace}.${id.path}")
    }

    lateinit var entityAttribute:  EntityAttribute

    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    abstract fun registerEvents()
    protected abstract fun shouldConsumeData(context: ModularEffectContext): Boolean
    abstract fun acceptTier(tier: DataModelTier): Boolean

    @ApiStatus.OverrideOnly
    protected open fun createEntityAttribute(): EntityAttribute {
        return ClampedEntityAttribute(name.key, 0.0, 0.0, DataModelTier.values().size.dec() * 4.0)
            .setTracked(true)
    }

    open fun createEntityAttributeModifier(armor: ModularArmorData): EntityAttributeModifier {
        return EntityAttributeModifier(id.toString(), armor.tier().ordinal.inc().toDouble(), EntityAttributeModifier.Operation.ADDITION)
    }

    @ApiStatus.Internal
    fun registerEntityAttribute() {
        entityAttribute = Registry.register(Registry.ATTRIBUTE, id, createEntityAttribute())
    }

    private fun canApply(context: ModularEffectContext): Boolean {
        return isEnabled()
            && acceptTier(context.tier)
            && context.dataModel.dataAmount >= applyCost()
    }

    private fun attemptConsumeData(context: ModularEffectContext) {
        if (shouldConsumeData(context)) {
            context.dataModel.dataAmount -= applyCost()
        }
    }

    fun attemptToApply(context: ModularEffectContext, body: () -> Unit) {
        if (canApply(context)) {
            attemptConsumeData(context)
            body()
        }
    }
}
