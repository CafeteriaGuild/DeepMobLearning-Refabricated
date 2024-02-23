package dev.nathanpb.dml.item.battery

import dev.nathanpb.dml.baseConfig
import net.minecraft.item.ItemStack
import java.awt.Color

class ItemEnergyOctahedron: AbstractItemBattery() {

    override fun getEnergyCapacity(stack: ItemStack) = baseConfig.misc.energyOctahedron.energyCapacity

    override fun getEnergyMaxInput(stack: ItemStack) = baseConfig.misc.energyOctahedron.energyIO

    override fun getEnergyMaxOutput(stack: ItemStack) = baseConfig.misc.energyOctahedron.energyIO

    override fun isPristineEnergy(): Boolean = false

    override fun getColor(): Color = Color.decode("#FCD904")

}