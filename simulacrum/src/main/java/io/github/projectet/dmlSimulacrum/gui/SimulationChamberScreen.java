package io.github.projectet.dmlSimulacrum.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.nathanpb.dml.enums.DataModelTier;
import io.github.projectet.dmlSimulacrum.block.entity.SimulationChamberEntity;
import io.github.projectet.dmlSimulacrum.dmlSimulacrum;
import io.github.projectet.dmlSimulacrum.util.Animation;
import io.github.projectet.dmlSimulacrum.util.DataModelUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Environment(EnvType.CLIENT)
public class SimulationChamberScreen extends HandledScreen<SimulationChamberScreenHandler> {

    public static final Identifier GUI = dmlSimulacrum.id( "textures/gui/simulation_chamber_base.png");
    public static final Identifier defaultGUI = dmlSimulacrum.id("textures/gui/default_gui.png");
    private static final int WIDTH = 232;
    private static final int HEIGHT = 230;
    private final double maxEnergy;
    SimulationChamberEntity blockEntity;
    private HashMap<String, Animation> animationList;
    private ItemStack currentDataModel = ItemStack.EMPTY;
    private final TextRenderer renderer;
    private final World world;
    private final SimulationChamberScreenHandler handler;

    public SimulationChamberScreen(SimulationChamberScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        blockEntity = (SimulationChamberEntity) MinecraftClient.getInstance().world.getBlockEntity(handler.blockPos);
        maxEnergy = blockEntity.energyStorage.getCapacity();
        animationList = new HashMap<>();
        world = blockEntity.getWorld();
        renderer = MinecraftClient.getInstance().textRenderer;
        this.handler = handler;
        backgroundWidth = WIDTH;
        backgroundHeight = HEIGHT;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        DecimalFormat f = new DecimalFormat("0.#");
        int x = this.x + 8;
        int spacing = 12;
        int yStart = y - 3;

        if(dataModelChanged()) {
            resetAnimations();
        }

        //Main Chamber GUI
        RenderSystem.setShaderTexture(0, GUI);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, x, y, 0, 0, 216, 141);

        drawTexture(matrices, x, y + 145, 0, 141, 18, 18);

        //Energy Bar Rendering
        int energyBarHeight = dmlSimulacrum.ensureRange((int) ( handler.getSyncedEnergy() / (maxEnergy - 64) * 87), 0, 87);
        int energyBarOffset = 87 - energyBarHeight;
        drawTexture(matrices, x + 203,  y + 48 + energyBarOffset, 25, 141, 7, energyBarHeight);

        String[] lines;

        if(!blockEntity.hasDataModel()) {
            lines = new String[] {"Please insert a data model", "to begin the simulation"};

            Animation a1 = getAnimation("pleaseInsert1");
            Animation a2 = getAnimation("pleaseInsert2");

            animateString(matrices, lines[0], a1, null, 1, false, x + 10, yStart + spacing, 0xFFFFFF);
            animateString(matrices, lines[1], a2, a1, 1, false, x + 10, yStart + (spacing * 2), 0xFFFFFF);

        } else if(DataModelUtil.getTier(blockEntity.getDataModel()).equals(DataModelTier.FAULTY)) {

            lines = new String[] {"Insufficient data in model", "please insert a basic model", "or better "};

            Animation insufData = getAnimation("insufData1");
            Animation insufData2 = getAnimation("insufData2");
            Animation insufData3 = getAnimation("insufData3");

            animateString(matrices, lines[0], insufData, null, 1, false, x + 10, yStart + spacing, 0xFFFFFF);
            animateString(matrices, lines[1], insufData2, insufData, 1, false,  x + 10, yStart + (spacing * 2), 0xFFFFFF);
            animateString(matrices, lines[2], insufData3, insufData2, 1, false,  x + 10, yStart + (spacing * 3), 0xFFFFFF);

        } else {
            // Draw current data model data
            if(DataModelUtil.getTier(blockEntity.getDataModel()).equals(DataModelTier.SELF_AWARE)) {
                drawTexture(matrices, x + 6,  y + 48, 18, 141, 7, 87);
            } else {
                int collectedData = DataModelUtil.getTierCount(blockEntity.getDataModel()) - DataModelUtil.getTier(blockEntity.getDataModel()).getDataAmount();
                int tierRoof = DataModelUtil.getTierRoof(blockEntity.getDataModel()) - DataModelUtil.getTier(blockEntity.getDataModel()).getDataAmount();

                int experienceBarHeight = (int) (((float) collectedData / tierRoof * 87));
                int experienceBarOffset = 87 - experienceBarHeight;
                drawTexture(matrices, x + 6,  y + 48 + experienceBarOffset, 18, 141, 7, experienceBarHeight);
            }

            drawTextWithShadow(matrices, renderer, new LiteralText("Tier: ").append(DataModelUtil.textTier(blockEntity.getDataModel())), x + 10, yStart + spacing, 0xFFFFFF);
            drawStringWithShadow(matrices, renderer, "Iterations: " + f.format(DataModelUtil.getSimulationCount(blockEntity.getDataModel())), x + 10, yStart + spacing * 2, 0xFFFFFF);
            drawStringWithShadow(matrices, renderer, "Pristine chance: " + dmlSimulacrum.pristineChance.get(DataModelUtil.getTier(blockEntity.getDataModel()).toString()) + "%", x + 10, yStart + spacing * 3, 0xFFFFFF);
        }

        // Draw player inventory
        RenderSystem.setShaderTexture(0, defaultGUI);
        drawTexture(matrices, x + 20, y + 145, 0, 0, 176, 90);


        drawConsoleText(matrices, x, y, spacing);
    }

    private void resetAnimations() {
        this.animationList = new HashMap<>();
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        int x = mouseX - this.x;
        int y = mouseY - this.y;

        NumberFormat f = NumberFormat.getNumberInstance(Locale.ENGLISH);
        List<Text> tooltip = new ArrayList<>();

        if(47 <= y && y < 135) {
            if(13 <= x && x < 22) {
                // Tooltip for data model data bar
                if(blockEntity.hasDataModel()) {
                    if(!DataModelUtil.getTier(blockEntity.getDataModel()).equals(DataModelTier.SELF_AWARE)) {
                        int currentTierCount = DataModelUtil.getTierCount(blockEntity.getDataModel()) - DataModelUtil.getTier(blockEntity.getDataModel()).getDataAmount();
                        int currentTierRoof = DataModelUtil.getTierRoof(blockEntity.getDataModel()) - DataModelUtil.getTier(blockEntity.getDataModel()).getDataAmount();
                        tooltip.add(new LiteralText(currentTierCount + "/" + currentTierRoof + " Data collected"));
                    } else {
                        tooltip.add(new LiteralText("This data model has reached the max tier."));
                    }
                } else {
                    tooltip.add(new LiteralText("Machine is missing a data model"));
                }
                renderTooltip(matrices, tooltip, x + 2, y + 2);
            } else if(211 <= x && x < 220) {
                // Tooltip for energy
                tooltip.add(new LiteralText(f.format(handler.getSyncedEnergy()) + "/" + f.format(maxEnergy) + " E"));
                if(blockEntity.hasDataModel()) {
                    int data = DataModelUtil.getEnergyCost(blockEntity.getDataModel());
                    tooltip.add(new LiteralText("Simulations with current data model drains " + f.format(data) + "E/t"));
                }
                renderTooltip(matrices, tooltip, x - 90, y - 16);
            }
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private Animation getAnimation(String key) {
        if (!animationList.containsKey(key)) {
            animationList.put(key, new Animation());
        }
        return animationList.get(key);
    }

    private void animateString(MatrixStack matrices, String string, Animation anim, Animation precedingAnim, int delay, boolean loop, int x, int y, int color) {
        if(precedingAnim != null) {
            if (precedingAnim.hasFinished()) {
                String result = anim.animate(string, delay, world.getLevelProperties().getTime(), loop);
                drawStringWithShadow(matrices, renderer, result, x, y, color);
            } else {
                return;
            }
        }
        String result = anim.animate(string, delay, world.getLevelProperties().getTime(), loop);
        drawStringWithShadow(matrices, renderer, result, x, y, color);
    }

    private void drawConsoleText(MatrixStack matrices, int x, int y, int spacing) {
        String[] lines;

        if(!blockEntity.hasDataModel() || DataModelUtil.getTier(blockEntity.getDataModel()).equals(DataModelTier.FAULTY)) {
            animateString(matrices,"_", getAnimation("blinkingUnderline"), null, 16, true, x + 21, y + 49, 0xFFFFFF);

        } else if(!blockEntity.hasPolymerClay() && !blockEntity.isCrafting()) {
            lines = new String[] {"Cannot begin simulation", "Missing polymer medium", "_"};
            Animation a1 = getAnimation("inputSlotEmpty1");
            Animation a2 = getAnimation("inputSlotEmpty2");
            Animation a3 = getAnimation("blinkingUnderline1");

            animateString(matrices, lines[0], a1, null, 1, false, x + 21, y + 51, 0xFFFFFF);
            animateString(matrices, lines[1], a2, a1, 1, false, x + 21, y + 51 + spacing, 0xFFFFFF);
            animateString(matrices, lines[2], a3, a2, 16, true, x + 21, y + 51 + (spacing * 2), 0xFFFFFF);

        } else if(!hasEnergy() && !blockEntity.isCrafting()) {
            lines = new String[] {"Cannot begin simulation", "System energy levels critical", "_"};
            Animation a1 = getAnimation("lowEnergy1");
            Animation a2 = getAnimation("lowEnergy2");
            Animation a3 = getAnimation("blinkingUnderline2");

            animateString(matrices, lines[0], a1, null, 1, false, x + 21, y + 51, 0xFFFFFF);
            animateString(matrices, lines[1], a2, a1, 1, false, x + 21, y + 51 + spacing, 0xFFFFFF);
            animateString(matrices, lines[2], a3, a2, 16, true, x + 21, y + 51 + (spacing * 2), 0xFFFFFF);
        } else if(blockEntity.outputIsFull() || blockEntity.pristineIsFull()) {
            lines = new String[] {"Cannot begin simulation", "Output or pristine buffer is full", "_"};
            Animation a1 = getAnimation("outputSlotFilled1");
            Animation a2 = getAnimation("outputSlotFilled2");
            Animation a3 = getAnimation("blinkingUnderline3");

            animateString(matrices, lines[0], a1, null, 1, false, x + 21, y + 51, 0xFFFFFF);
            animateString(matrices, lines[1], a2, a1, 1, false, x + 21, y + 51 + spacing, 0xFFFFFF);
            animateString(matrices, lines[2], a3, a2, 16, true, x + 21, y + 51 + (spacing * 2), 0xFFFFFF);
        } else if(blockEntity.isCrafting()) {
            drawStringWithShadow(matrices, renderer, blockEntity.percentDone + "%", x + 176, y + 123, 0x62D8FF);

            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("simulationProgressLine1"), x + 21, y + 51, 0xFFFFFF);
            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("simulationProgressLine1Version"), x + 124, y + 51, 0xFFFFFF);

            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("simulationProgressLine2"), x + 21, y + 51 + spacing, 0xFFFFFF);

            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("simulationProgressLine3"), x + 21, y + 51 + (spacing * 2), 0xFFFFFF);
            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("simulationProgressLine4"), x + 21, y + 51 + (spacing * 3), 0xFFFFFF);
            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("simulationProgressLine5"), x + 21, y + 51 + (spacing * 4), 0xFFFFFF);

            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("simulationProgressLine6"), x + 21, y + 51 + (spacing * 5), 0xFFFFFF);
            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("simulationProgressLine6Result"), x + 140, y + 51 + (spacing * 5), 0xFFFFFF);

            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("simulationProgressLine7"), x + 21, y + 51 + (spacing * 6), 0xFFFFFF);
            drawStringWithShadow(matrices, renderer, blockEntity.getSimulationText("blinkingDots1"), x + 128, y + 51 + (spacing * 6), 0xFFFFFF);
        } else {
            animateString(matrices, "_", getAnimation("blinkingUnderline"), null, 16, true, x + 21, y + 49, 0xFFFFFF);
        }
    }

    private boolean hasEnergy() {
        return blockEntity.hasEnergyForSimulation();
    }

    private boolean dataModelChanged() {
        if(ItemStack.areItemsEqual(currentDataModel, blockEntity.getDataModel())) {
            return false;
        } else {
            this.currentDataModel = blockEntity.getDataModel();
            return true;
        }
    }
}
