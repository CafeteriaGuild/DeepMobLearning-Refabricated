package io.github.projectet.dmlSimulacrum;

import dev.nathanpb.dml.item.ItemDataModel;
import io.github.projectet.dmlSimulacrum.gui.SimulationChamberScreen;
import io.github.projectet.dmlSimulacrum.gui.SimulationChamberScreenHandler;
import io.github.projectet.dmlSimulacrum.util.DataModelUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class dmlSimulacrumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(SimulationChamberScreenHandler.SCS_HANDLER_TYPE, SimulationChamberScreen::new);

        ItemTooltipCallback.EVENT.register((item, context, lines) -> {
            if(item.getItem() instanceof ItemDataModel && DataModelUtil.getEntityCategory(item) != null) {
                lines.add(new TranslatableText("Simulation Cost: %s §7E/t", "§7" + DataModelUtil.getEnergyCost(item)));
                lines.add(new LiteralText("Type: ").append(DataModelUtil.textType(item)));
            }
        });
    }
}
