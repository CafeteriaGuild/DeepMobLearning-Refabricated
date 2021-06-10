package dev.nathanpb.dml.data

import dev.nathanpb.dml.data.serializers.InventorySerializer
import dev.nathanpb.ktdatatag.data.MutableCompoundData
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

class DeepLearnerData(val stack: ItemStack) : MutableCompoundData(stack.orCreateTag) {

    var inventory by persistentDefaulted(
        DefaultedList.ofSize(4, ItemStack.EMPTY),
        InventorySerializer(4)
    )

}
