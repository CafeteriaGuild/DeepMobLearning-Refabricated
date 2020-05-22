package dev.nathanpb.dml.data

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

import dev.nathanpb.dml.InvalidTrialKeyBase
import dev.nathanpb.dml.item.ItemDataModel
import dev.nathanpb.dml.item.ItemTrialKey
import net.minecraft.entity.EntityType
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag

data class TrialKeyData (val entity: EntityType<*>, val dataAmount: Int = 0) {

    companion object {
        const val DATA_TAG_KEY = "deepmoblearning.trialkey"
        const val ENTITY_TAG_KEY = "entity"
        const val DATA_AMOUNT_TAG_KEY = "dataAmount"

        fun fromStack(stack: ItemStack) = when (stack.item) {
            is ItemTrialKey -> stack.getSubTag(DATA_TAG_KEY)?.let {
                TrialKeyData(
                    EntityType.get(it.getString(ENTITY_TAG_KEY)).orElseThrow {
                        InvalidTrialKeyBase()
                    },
                    it.getInt(DATA_AMOUNT_TAG_KEY)
                )
            }
            is ItemDataModel -> fromDataModelData(stack.dataModel)
            else -> throw InvalidTrialKeyBase()
        }

        fun fromDataModelData(data: DataModelData) = data.entity?.let {
            TrialKeyData(it, data.dataAmount)
        }
    }

    fun tier() = DataModelTier.fromDataAmount(dataAmount)
    fun toTag() = CompoundTag().apply {
        putString(ENTITY_TAG_KEY, EntityType.getId(entity).toString())
        putInt(DATA_AMOUNT_TAG_KEY, dataAmount)
    }
}

var ItemStack.trialKeyData : TrialKeyData?
    get() = TrialKeyData.fromStack(this)
    set(value) = if (value != null) {
        this.putSubTag(TrialKeyData.DATA_TAG_KEY, value.toTag())
    } else {
        this.removeSubTag(TrialKeyData.DATA_TAG_KEY)
    }
