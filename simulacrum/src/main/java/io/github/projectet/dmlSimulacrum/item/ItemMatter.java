package io.github.projectet.dmlSimulacrum.item;

import dev.nathanpb.dml.utils.RenderUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemMatter extends Item {

    int experience;
    public ItemMatter(Settings settings, int experience) {
        super(settings.rarity(Rarity.UNCOMMON));
        this.experience = experience;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(!world.isClient) {
            int count = 1;
            if(user.isSneaking()) {
                count = stack.getCount();
                stack.decrement(count);
            }
            else {
                stack.decrement(1);
                user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 0.1f, (float) Math.random());
            }
            user.addExperience(count * experience);
        }
        return TypedActionResult.consume(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(world == null) return;
        tooltip.add(RenderUtils.Companion.getTextWithDefaultTextColor(Text.translatable("tooltip.dmlsimulacrum.matter.1"), world)
                .append(Text.translatable("tooltip.dmlsimulacrum.matter.2").formatted(Formatting.WHITE)));
        tooltip.add(Text.translatable("tooltip.dmlsimulacrum.matter.3")
                .append(RenderUtils.Companion.getTextWithDefaultTextColor(Text.translatable("tooltip.dmlsimulacrum.matter.4"), world)));
        tooltip.add(RenderUtils.Companion.getTextWithDefaultTextColor(Text.translatable("tooltip.dmlsimulacrum.matter.5"), world)
                .append(Text.of(String.valueOf(experience)).copy().formatted(Formatting.WHITE)));
    }
}
