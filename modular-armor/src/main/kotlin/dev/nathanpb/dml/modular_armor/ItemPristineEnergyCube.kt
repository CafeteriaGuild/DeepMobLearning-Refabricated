package dev.nathanpb.dml.modular_armor

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.battery.AbstractItemBattery
import dev.nathanpb.dml.utils.ITEM_PRISTINE
import dev.nathanpb.dml.utils.RenderUtils.Companion.ALT_STYLE
import dev.nathanpb.dml.utils.RenderUtils.Companion.STYLE
import dev.nathanpb.dml.utils.getEnergyStorage
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Style
import team.reborn.energy.api.EnergyStorage
import java.awt.Color

class ItemPristineEnergyCube : AbstractItemBattery() {

    companion object {
        val PRISTINE_ENERGY_CUBE = ItemPristineEnergyCube()

        fun register() {
            Registry.register(Registries.ITEM, identifier("pristine_energy_cube"), PRISTINE_ENERGY_CUBE)

            ITEM_PRISTINE.registerForItems(::getEnergyStorage, PRISTINE_ENERGY_CUBE)
            EnergyStorage.ITEM.registerForItems({ _, _ ->
                return@registerForItems EnergyStorage.EMPTY
            }, PRISTINE_ENERGY_CUBE)
        }
    }

    override fun getEnergyCapacity(stack: ItemStack): Long = 32768L

    override fun getEnergyMaxInput(stack: ItemStack): Long = 4096L

    override fun getEnergyMaxOutput(stack: ItemStack): Long = 4096L

    override fun isPristineEnergy(): Boolean = true

    override fun getColor(): Color = Color.decode("#04FCC4")

    override val primaryStyle: Style = STYLE
    override val secondaryStyle: Style = ALT_STYLE

}