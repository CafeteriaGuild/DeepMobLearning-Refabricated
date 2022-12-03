package io.github.projectet.dmlSimulacrum.gui;

import io.github.projectet.dmlSimulacrum.block.entity.SimulationChamberEntity;
import io.github.projectet.dmlSimulacrum.dmlSimulacrum;
import io.github.projectet.dmlSimulacrum.inventory.SlotSimulationChamber;
import io.github.projectet.dmlSimulacrum.util.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SimulationChamberScreenHandler extends ScreenHandler implements Constants {

    private Inventory inventory;
    private final PlayerEntity player;
    private SimulationChamberEntity blockEntity;
    public BlockPos blockPos;
    private World world;
    PropertyDelegate propertyDelegate;

    public static final ScreenHandlerType<SimulationChamberScreenHandler> SCS_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(dmlSimulacrum.id("simulation"), SimulationChamberScreenHandler::new);

    public SimulationChamberScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf packetByteBuf) {
        super(SCS_HANDLER_TYPE, syncId);
        this.player = playerInventory.player;
        this.world = this.player.world;
        this.blockPos = packetByteBuf.readBlockPos();
        this.blockEntity = ((SimulationChamberEntity) playerInventory.player.getEntityWorld().getBlockEntity(blockPos));
        this.inventory = blockEntity;
        this.propertyDelegate = blockEntity.propertyDelegate;
        checkSize(inventory, 4);
        if(world.isClient) addProperties(propertyDelegate);
        addSlots();
        addInventorySlots();
    }

    public SimulationChamberScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, SimulationChamberEntity blockEntity) {
        this(syncId, playerInventory, PacketByteBufs.create().writeBlockPos(blockEntity.getPos()));
    }

    public int getSyncedEnergy(){
        return propertyDelegate.get(0);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addSlots() {
        addSlot(new SlotSimulationChamber(inventory, DATA_MODEL_SLOT, 9, 146));
        addSlot(new SlotSimulationChamber(inventory, INPUT_SLOT, 176, 7));
        addSlot(new SlotSimulationChamber(inventory, OUTPUT_SLOT, 196, 7));
        addSlot(new SlotSimulationChamber(inventory, PRISTINE_SLOT, 186, 27));
    }

    private void addInventorySlots() {
        // Bind actionbar
        for (int row = 0; row < 9; row++) {
            Slot slot = new Slot(player.getInventory(), row, 36 + row * 18, 211);
            addSlot(slot);
        }

        // 3 Top rows, starting with the bottom one
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int x = 36 + column * 18;
                int y = 153 + row * 18;
                int index = column + row * 9 + 9;
                Slot slot = new Slot(player.getInventory(), index, x, y);
                addSlot(slot);
            }
        }
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        if(!world.isClient) {
            // Update the BE every tick while container is open
            blockEntity.updateState();
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        if(!world.isClient) {
            Slot slot = this.slots.get(index);
            if (slot != null && slot.hasStack()) {
                ItemStack originalStack = slot.getStack();
                newStack = originalStack.copy();
                if (index < this.inventory.size()) {
                    if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                    return ItemStack.EMPTY;
                }

                if (originalStack.isEmpty()) {
                    slot.setStack(ItemStack.EMPTY);
                } else {
                    slot.markDirty();
                }
            }
        }

        return newStack;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(!world.isClient) {
            super.onSlotClick(slotIndex, button, actionType, player);
        }
    }
}
