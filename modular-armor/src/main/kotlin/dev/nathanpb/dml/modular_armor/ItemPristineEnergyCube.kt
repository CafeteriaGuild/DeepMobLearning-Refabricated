package dev.nathanpb.dml.modular_armor

import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.item.battery.AbstractItemBattery
import dev.nathanpb.dml.itemgroup.ITEM_GROUP_KEY
import dev.nathanpb.dml.utils.ITEM_PRISTINE
import dev.nathanpb.dml.utils.RenderUtils.Companion.ALT_STYLE
import dev.nathanpb.dml.utils.RenderUtils.Companion.STYLE
import dev.nathanpb.dml.utils.getEmptyAndFullCapacityEnergyItem
import dev.nathanpb.dml.utils.getEnergyStorage
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Style
import team.reborn.energy.api.EnergyStorage
import java.awt.Color

class ItemPristineEnergyCube : AbstractItemBattery() {

    companion object {
        val ITEM_PRISTINE_ENERGY_CUBE = ItemPristineEnergyCube()

        fun register() {
            Registry.register(Registries.ITEM, identifier("pristine_energy_cube"), ITEM_PRISTINE_ENERGY_CUBE)

            ITEM_PRISTINE.registerForItems(::getEnergyStorage, ITEM_PRISTINE_ENERGY_CUBE)
            EnergyStorage.ITEM.registerForItems({ _, _ ->
                return@registerForItems EnergyStorage.EMPTY
            }, ITEM_PRISTINE_ENERGY_CUBE)
            ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register {
                getEmptyAndFullCapacityEnergyItem(ITEM_PRISTINE_ENERGY_CUBE).forEach { stack ->
                    it.addAfter(ItemStack(BlockMatterCondenser.BLOCK_MATTER_CONDENSER), stack)
                }
            }

        }
    }

    override fun getEnergyCapacity(stack: ItemStack) = modularArmorConfig.misc.pristineEnergyCube.energyCapacity

    override fun getEnergyMaxInput(stack: ItemStack) = modularArmorConfig.misc.pristineEnergyCube.energyIO

    override fun getEnergyMaxOutput(stack: ItemStack) = modularArmorConfig.misc.pristineEnergyCube.energyIO

    override fun isPristineEnergy(): Boolean = true

    override fun getColor(): Color = Color.decode("#04FCC4")

    override val primaryStyle: Style = STYLE
    override val secondaryStyle: Style = ALT_STYLE

}