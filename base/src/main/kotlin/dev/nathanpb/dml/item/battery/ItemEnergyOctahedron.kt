package dev.nathanpb.dml.item.battery

import net.minecraft.item.ItemStack
import java.awt.Color

class ItemEnergyOctahedron: AbstractItemBattery() {

    override fun getEnergyCapacity(stack: ItemStack): Long = 32768L

    override fun getEnergyMaxInput(stack: ItemStack): Long = 4096L

    override fun getEnergyMaxOutput(stack: ItemStack): Long = 4096L

    override fun isPristineEnergy(): Boolean = false

    override fun getColor(): Color = Color.decode("#FCD904")

}