/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

package dev.nathanpb.dml.entity

import dev.nathanpb.dml.blockEntity.BlockEntityTrialKeystone
import dev.nathanpb.dml.data.DataModelTier
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.world.World

class SystemGlitchEntity(type: EntityType<out HostileEntity>, world: World) : HostileEntity(type, world) {
    companion object {
        fun createMobAttributes(): DefaultAttributeContainer.Builder = LivingEntity.createLivingAttributes()
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, BlockEntityTrialKeystone.EFFECTIVE_AREA_RADIUS * 2)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
    }

    var tier: DataModelTier? = null
}
