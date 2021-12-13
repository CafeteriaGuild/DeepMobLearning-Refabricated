package dev.nathanpb.dml.inventory;

import dev.nathanpb.dml.item.ItemDataModel;
import dev.nathanpb.dml.item.ItemPolymerClay;
import dev.nathanpb.dml.util.Constants;
import dev.nathanpb.dml.util.DataModelUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class SlotSimulationChamber extends Slot implements Constants {
    private final int index;

    public SlotSimulationChamber(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.index = index;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        Item item = stack.getItem();
        return switch (index) {
            case DATA_MODEL_SLOT -> !stack.isEmpty() && item instanceof ItemDataModel && DataModelUtil.getEntityCategory(stack) != null;
            case INPUT_SLOT -> !stack.isEmpty() && item instanceof ItemPolymerClay;
            default -> false;
        };
    }
}
