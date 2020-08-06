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
import dev.nathanpb.dml.data.serializers.TrialAffixListSerializer
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.ktdatatag.data.MutableCompoundData
import dev.nathanpb.ktdatatag.serializer.EnumSerializer
import dev.nathanpb.ktdatatag.serializer.Serializers
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag

class TrialKeyData (tag: CompoundTag) : MutableCompoundData(tag) {

    constructor(stack: ItemStack) : this(stack.getOrCreateSubTag(TAG_KEY))

    companion object {
        const val TAG_KEY = "${MOD_ID}.trialkey"

        fun fromDataModelData(data: DataModelData) = data.category?.let {
            TrialKeyData(CompoundTag()).apply {
                category = it
                dataAmount = data.dataAmount
             }
        }
    }

    var category by persistentDefaulted(EntityCategory.END, EnumSerializer(EntityCategory::class.java))
    var dataAmount by persistentDefaulted(0, Serializers.INT)
    var affixes by persistentDefaulted(emptyList(), TrialAffixListSerializer())

    fun tier() = DataModelTier.fromDataAmount(dataAmount)

}

var ItemStack.trialKeyData : TrialKeyData?
    get() = if (orCreateTag.contains(TrialKeyData.TAG_KEY)) {
        TrialKeyData(this)
    } else null
    set(value) = if (value != null) {
        this.putSubTag(TrialKeyData.TAG_KEY, value.tag)
    } else {
        this.removeSubTag(TrialKeyData.TAG_KEY)
    }
