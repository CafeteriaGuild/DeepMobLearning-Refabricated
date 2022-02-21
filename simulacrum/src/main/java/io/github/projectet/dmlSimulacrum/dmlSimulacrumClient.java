package io.github.projectet.dmlSimulacrum;

import dev.nathanpb.dml.item.ItemDataModel;
import dev.nathanpb.dml.utils.RenderUtils;
import io.github.projectet.dmlSimulacrum.gui.SimulationChamberScreen;
import io.github.projectet.dmlSimulacrum.gui.SimulationChamberScreenHandler;
import io.github.projectet.dmlSimulacrum.util.DataModelUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class dmlSimulacrumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(SimulationChamberScreenHandler.SCS_HANDLER_TYPE, SimulationChamberScreen::new);

        ItemTooltipCallback.EVENT.register((item, context, lines) -> {
            World world = MinecraftClient.getInstance().world;
            if(item.getItem() instanceof ItemDataModel && DataModelUtil.getEntityCategory(item) != null && world != null) {
                lines.add(RenderUtils.Companion.getTextWithDefaultTextColor(new TranslatableText("tooltip.dmlsimulacrum.data_model.1"), world)
                        .append(new TranslatableText("tooltip.dmlsimulacrum.data_model.2", DataModelUtil.getEnergyCost(item)).formatted(Formatting.WHITE)));
                lines.add(RenderUtils.Companion.getTextWithDefaultTextColor(new TranslatableText("tooltip.dmlsimulacrum.data_model.3"), world)
                        .append(new TranslatableText("tooltip.dmlsimulacrum.data_model.4", DataModelUtil.textType(item)).formatted(Formatting.WHITE)));

            }
        });
    }
}
