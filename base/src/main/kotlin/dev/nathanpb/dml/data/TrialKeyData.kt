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

package dev.nathanpb.dml.data

import dev.nathanpb.dml.MOD_ID
import dev.nathanpb.dml.data.serializers.EntityCategorySerializer
import dev.nathanpb.dml.data.serializers.TrialAffixListSerializer
import dev.nathanpb.dml.entityCategory.EntityCategory
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.utils.takeOrNull
import dev.nathanpb.ktdatatag.data.MutableCompoundData
import dev.nathanpb.ktdatatag.serializer.Serializers
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound

class TrialKeyData (tag: NbtCompound) : MutableCompoundData(tag) {

    constructor(stack: ItemStack) : this(stack.getOrCreateSubNbt(TAG_KEY))

    companion object {
        const val TAG_KEY = "${MOD_ID}.trialkey"

        fun fromDataModelData(data: DataModelData) = data.category?.let {
            TrialKeyData(NbtCompound()).apply {
                category = it
                dataAmount = data.dataAmount
             }
        }
    }

    var category by persistentDefaulted(EntityCategory.END, EntityCategorySerializer())
    var dataAmount by persistentDefaulted(0, Serializers.INT)
    var affixes by persistentDefaulted(emptyList(), TrialAffixListSerializer())

    fun tier() = DataModelTier.fromDataAmount(dataAmount)

}

var ItemStack.trialKeyData : TrialKeyData?
    get() = takeOrNull(orCreateNbt.contains(TrialKeyData.TAG_KEY)) {
        TrialKeyData(this)
    }
    set(value) = if (value != null) {
        this.setSubNbt(TrialKeyData.TAG_KEY, value.tag)
    } else {
        this.removeSubNbt(TrialKeyData.TAG_KEY)
    }
