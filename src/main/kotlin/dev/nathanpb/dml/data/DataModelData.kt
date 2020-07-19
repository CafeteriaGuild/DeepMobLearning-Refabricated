package dev.nathanpb.dml.data

import dev.nathanpb.dml.NotDataModelException
import dev.nathanpb.dml.item.ItemDataModel
import net.minecraft.item.ItemStack

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */


class DataModelData(val stack: ItemStack) {

    companion object {
        const val CATEGORY_TAG_KEY = "deepmoblearning.datamodel.category"
        const val DATA_AMOUNT_TAG_KEY = "deepmoblearning.datamodel.dataAmount"
    }

    init {
        if (stack.item !is ItemDataModel) {
            throw NotDataModelException()
        }
    }

    var category: EntityCategory?
        get() {
            stack.orCreateTag.getString(CATEGORY_TAG_KEY)?.let {
                return if (it.isNotEmpty())  EntityCategory.valueOf(it) else null
            }
            return null
        }
        set(value) {
            stack.orCreateTag.putString(CATEGORY_TAG_KEY, value?.name)
        }

    var dataAmount: Int
        get() = stack.orCreateTag.getInt(DATA_AMOUNT_TAG_KEY)
        set(value) = stack.orCreateTag.putInt(DATA_AMOUNT_TAG_KEY, value)

    fun tier() = DataModelTier.fromDataAmount(dataAmount)

}

val ItemStack.dataModel: DataModelData
    get() = DataModelData(this)
