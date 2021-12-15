package io.github.projectet.dmlSimulacrum.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemMatter extends Item {

    int experience;
    public ItemMatter(Settings settings, int experience) {
        super(settings);
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
        tooltip.add(new LiteralText("§7Can be consumed for experience §r(Right click)"));
        tooltip.add(new LiteralText("§7Hold §rSHIFT§7 to consume entire stack."));
        tooltip.add(new LiteralText("§7Experience per item: ").append(new LiteralText(String.valueOf(experience)).formatted(Formatting.GREEN)));
    }
}
