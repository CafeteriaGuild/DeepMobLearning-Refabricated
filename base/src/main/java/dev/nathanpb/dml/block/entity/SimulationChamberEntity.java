package dev.nathanpb.dml.block.entity;

import dev.nathanpb.dml.item.ItemDataModel;
import dev.nathanpb.dml.item.ItemPristineMatter;
import dev.nathanpb.dml.dmlSimulacrum;
import dev.nathanpb.dml.gui.SimulationChamberScreenHandler;
import dev.nathanpb.dml.inventory.ImplementedInventory;
import dev.nathanpb.dml.item.ItemMatter;
import dev.nathanpb.dml.item.ItemPolymerClay;
import dev.nathanpb.dml.util.Animation;
import dev.nathanpb.dml.util.Constants;
import dev.nathanpb.dml.util.DataModelUtil;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.HashMap;
import java.util.Random;

public class SimulationChamberEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory, Constants, SidedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    public int ticks = 0;
    public int percentDone = 0;
    private boolean isCrafting = false;
    private boolean byproductSuccess = false;
    private String currentDataModelType = "";
    public SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(2000000, 25600, 0);

    private HashMap<String, String> simulationText = new HashMap<>();
    private HashMap<String, Animation> simulationAnimations = new HashMap<>();

    public PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return (int) energyStorage.getAmount();
        }

        @Override
        public void set(int index, int value) {
            energyStorage.amount = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public SimulationChamberEntity(BlockPos pos, BlockState state) {
        super(dmlSimulacrum.SIMULATION_CHAMBER_ENTITY, pos, state);
    }

    private static boolean dataModelMatchesOutput(ItemStack stack, ItemStack output) {
        Item livingMatter = dataModel.get(DataModelUtil.getEntityCategory(stack).toString()).getType().getItem();
        return Registry.ITEM.getId(livingMatter).equals(Registry.ITEM.getId(output.getItem()));
    }

    private static boolean dataModelMatchesPristine(ItemStack stack, ItemStack pristine) {
        Item pristineMatter = dataModel.get(DataModelUtil.getEntityCategory(stack).toString()).getPristine();
        return Registry.ITEM.getId(pristineMatter).equals(Registry.ITEM.getId(pristine.getItem()));
    }

    public static void tick(World world, BlockPos pos, BlockState state, SimulationChamberEntity blockEntity) {
        blockEntity.ticks++;
        if(!world.isClient) {
            if(!blockEntity.isCrafting()) {
                if(blockEntity.canStartSimulation()) {
                    blockEntity.startSimulation();
                }
            } else {
                if (!blockEntity.canContinueSimulation() || blockEntity.dataModelTypeChanged()) {
                    blockEntity.finishSimulation(true);
                    return;
                }

                blockEntity.updateSimulationText(blockEntity.getDataModel());

                if (blockEntity.percentDone == 0) {
                    Random rand = new Random();
                    int num = rand.nextInt(100);
                    int chance = dmlSimulacrum.pristineChance.get(DataModelUtil.getTier(blockEntity.getDataModel()).toString());
                    blockEntity.byproductSuccess = num <= dmlSimulacrum.ensureRange(chance, 1, 100);
                }

                int energyTickCost = DataModelUtil.getEnergyCost(blockEntity.getDataModel());
                blockEntity.energyStorage.amount = blockEntity.energyStorage.amount - energyTickCost;

                if (blockEntity.ticks % ((20 * 15) / 100) == 0) {
                    blockEntity.percentDone++;
                }

                // Notify while crafting every other second, this is done more frequently when the container is open
                if (blockEntity.ticks % (20 * 2) == 0) {
                    blockEntity.updateState();
                }
            }

            if(blockEntity.percentDone == 100) {
                blockEntity.finishSimulation(false);
                return;
            }

            blockEntity.markDirty();
        }
    }

    public void updateState() {
        BlockState state = world.getBlockState(getPos());
        world.updateListeners(getPos(), state, state, 3);
    }

    public boolean isCrafting() {
        return isCrafting;
    }

    private boolean dataModelTypeChanged() {
        return !currentDataModelType.equals(DataModelUtil.getEntityCategory(getDataModel()).toString());
    }

    public NbtCompound createTagFromSimText() {
        NbtCompound tag = new NbtCompound();
        simulationText.forEach(tag::putString);
        return tag;
    }

    public void getSimTextfromTag(NbtCompound tag) {
        simulationText.forEach((key, text) -> simulationText.put(key, tag.getString(key)));
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        energyStorage.amount = compound.getLong("energy");
        byproductSuccess = compound.getBoolean("byproductSuccess");
        isCrafting = compound.getBoolean("isCrafting");
        percentDone = compound.getInt("percentDone");
        currentDataModelType = compound.getString("currentDataModelType");
        getSimTextfromTag(compound.getCompound("simulationText"));
        Inventories.readNbt(compound, inventory);
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.putLong("energy", energyStorage.amount);
        compound.putBoolean("byproductSuccess", byproductSuccess);
        compound.putBoolean("isCrafting", isCrafting);
        compound.putInt("percentDone", percentDone);
        compound.putString("currentDataModelType", currentDataModelType);
        compound.put("simulationText", createTagFromSimText());
        Inventories.writeNbt(compound, inventory);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if(!this.world.isClient) {
            ((ServerChunkManager) world.getChunkManager()).markForUpdate(getPos());
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }

    private void updateSimulationText(ItemStack stack) {
        String[] lines = new String[]{
                "> Launching runtime",
                "v1.4.7",
                "> Iteration #" + (DataModelUtil.getSimulationCount(stack) + 1) + " started",
                "> Loading model from chip memory",
                "> Assessing threat level",
                "> Engaged enemy",
                "> Pristine procurement",
                byproductSuccess ? "succeeded" : "failed",
                "> Processing results",
                "..."
        };

        String resultPrefix = byproductSuccess ? "§a" : "§c";

        Animation aLine1 = getAnimation("simulationProgressLine1");
        Animation aLine1Version = getAnimation("simulationProgressLine1Version");

        Animation aLine2 = getAnimation("simulationProgressLine2");

        Animation aLine3 = getAnimation("simulationProgressLine3");
        Animation aLine4 = getAnimation("simulationProgressLine4");
        Animation aLine5 = getAnimation("simulationProgressLine5");

        Animation aLine6 = getAnimation("simulationProgressLine6");
        Animation aLine6Result = getAnimation("simulationProgressLine6Result");

        Animation aLine7 = getAnimation("simulationProgressLine7");
        Animation aLine8 = getAnimation("blinkingDots1");

        simulationText.put("simulationProgressLine1", animate(lines[0], aLine1, null, 1, false));
        simulationText.put("simulationProgressLine1Version", "§6" + animate(lines[1], aLine1Version, aLine1, 1, false) + "§r");

        simulationText.put("simulationProgressLine2", animate(lines[2], aLine2, aLine1Version, 1, false));

        simulationText.put("simulationProgressLine3", animate(lines[3], aLine3, aLine2, 2, false));
        simulationText.put("simulationProgressLine4", animate(lines[4], aLine4, aLine3, 1, false));
        simulationText.put("simulationProgressLine5", animate(lines[5], aLine5, aLine4, 2, false));

        simulationText.put("simulationProgressLine6", animate(lines[6], aLine6, aLine5, 2, false));
        simulationText.put("simulationProgressLine6Result", resultPrefix + animate(lines[7], aLine6Result, aLine6, 2, false) + "§r");

        simulationText.put("simulationProgressLine7", animate(lines[8], aLine7, aLine6Result, 1, false));
        simulationText.put("blinkingDots1", animate(lines[9], aLine8, aLine7, 8, true));
    }

    private String animate(String string, Animation anim, @Nullable Animation precedingAnim, int delayInTicks, boolean loop) {
        if(precedingAnim != null) {
            if (precedingAnim.hasFinished()) {
                return anim.animate(string, delayInTicks, world.getLevelProperties().getTime(), loop);
            } else {
                return "";
            }
        }
        return  anim.animate(string, delayInTicks, world.getLevelProperties().getTime(), loop);
    }

    private Animation getAnimation(String key) {
        if (!simulationAnimations.containsKey(key)) {
            simulationAnimations.put(key, new Animation());
        }
        return simulationAnimations.get(key);
    }

    public String getSimulationText(String key) {
        if (!simulationText.containsKey(key)) {
            simulationText.put(key, "");
        }
        return simulationText.get(key);
    }

    private void startSimulation() {
        isCrafting = true;
        currentDataModelType = DataModelUtil.getEntityCategory(getDataModel()).toString();
        inventory.get(1).setCount(getPolymerClay().getCount() - 1);
        resetAnimations();
    }

    private void finishSimulation(boolean abort) {
        resetAnimations();
        percentDone = 0;
        isCrafting = false;
        // Only decrease input and increase output if not aborted, and only if on the server's TE
        if(!abort && !world.isClient) {
            DataModelUtil.updateSimulationCount(getDataModel());
            DataModelUtil.updateTierCount(getDataModel());

            if(inventory.get(2).getItem() instanceof ItemMatter) inventory.get(2).setCount(getLiving().getCount() + 1);
            else inventory.set(2, new ItemStack(dataModel.get(currentDataModelType).getType().getItem(), 1));

            if(byproductSuccess) {
                // If Byproduct roll was successful
                byproductSuccess = false;
                if(inventory.get(3).getItem() instanceof ItemPristineMatter) inventory.get(3).increment(1);
                else inventory.set(3, new ItemStack(dataModel.get(currentDataModelType).getPristine(), 1));
            }

            updateState();
        }
    }

    private boolean canStartSimulation() {
        return hasEnergyForSimulation() && canContinueSimulation() && !outputIsFull() && !pristineIsFull() && hasPolymerClay();
    }

    private boolean canContinueSimulation() {
        return hasDataModel() && !DataModelUtil.getTier(getDataModel()).toString().equalsIgnoreCase("faulty");
    }

    public boolean hasEnergyForSimulation() {
        if(hasDataModel()) {
            int ticksPerSimulation = 300;
            return energyStorage.amount > ((long) ticksPerSimulation * DataModelUtil.getEnergyCost(getDataModel()));
        } else {
            return false;
        }
    }

    public void resetAnimations() {
        simulationAnimations = new HashMap<>();
        simulationText = new HashMap<>();
    }

    public ItemStack getDataModel() {
        return getStack(DATA_MODEL_SLOT);
    }

    private ItemStack getPolymerClay() {
        return getStack(INPUT_SLOT);
    }

    private ItemStack getLiving() {
        return getStack(OUTPUT_SLOT);
    }

    private ItemStack getPristine() {
        return getStack(PRISTINE_SLOT);
    }

    public boolean hasDataModel() {
        return getDataModel().getItem() instanceof ItemDataModel;
    }

    public boolean hasPolymerClay() {
        ItemStack stack = getPolymerClay();
        return stack.getItem() instanceof ItemPolymerClay && stack.getCount() > 0;
    }

    public boolean outputIsFull() {
        ItemStack stack = getLiving();
        if(stack.isEmpty()) {
            return false;
        }

        boolean stackLimitReached = stack.getCount() == getLiving().getMaxCount();
        boolean outputMatches = dataModelMatchesOutput(getDataModel(), getLiving());

        return stackLimitReached || !outputMatches;
    }

    public boolean pristineIsFull() {
        ItemStack stack = getPristine();
        if(stack.isEmpty()) {
            return false;
        }

        boolean stackLimitReached = stack.getCount() == inventory.get(3).getMaxCount();
        boolean outputMatches = dataModelMatchesPristine(getDataModel(), getPristine());

        return stackLimitReached || !outputMatches;
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Simulation Chamber");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new SimulationChamberScreenHandler(syncId, inv, this, this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(getPos());
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return new int[]{DATA_MODEL_SLOT, INPUT_SLOT};
        }
        return new int[]{OUTPUT_SLOT, PRISTINE_SLOT};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir == Direction.UP) {
            return switch (slot) {
                case DATA_MODEL_SLOT -> stack.getItem() instanceof ItemDataModel && DataModelUtil.getEntityCategory(stack) != null;
                case INPUT_SLOT -> stack.getItem() instanceof ItemPolymerClay;
                default -> false;
            };
        }
        else return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if(dir != Direction.UP) {
            return switch (slot) {
                case OUTPUT_SLOT, PRISTINE_SLOT -> true;
                default -> false;
            };
        }
        else return false;
    }
}
