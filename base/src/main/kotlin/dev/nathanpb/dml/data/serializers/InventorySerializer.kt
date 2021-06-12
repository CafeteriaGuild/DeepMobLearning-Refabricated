package dev.nathanpb.dml.data.serializers

import dev.nathanpb.ktdatatag.serializer.DataSerializer
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList


class InventorySerializer(val size: Int) : DataSerializer<DefaultedList<ItemStack>> {

    override fun write(tag: NbtCompound, key: String, data: DefaultedList<ItemStack>) {
        tag.put(key, Inventories.writeNbt(NbtCompound(), data))
    }

    override fun read(tag: NbtCompound, key: String): DefaultedList<ItemStack> {
        return DefaultedList.ofSize(size, ItemStack.EMPTY).also {
            Inventories.readNbt(tag.getCompound(key), it)
        }
    }
}
