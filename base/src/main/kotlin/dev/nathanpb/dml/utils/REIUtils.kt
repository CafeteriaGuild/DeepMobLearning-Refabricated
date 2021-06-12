package dev.nathanpb.dml.utils

import me.shedaniel.rei.api.common.entry.EntryStack
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack


fun  EntryStack<*>.itemStack(): ItemStack {
    return value.let { value ->
        when (value) {
            is ItemStack -> value
            is ItemConvertible -> ItemStack(value)
            else -> ItemStack.EMPTY
        }
    }
}
