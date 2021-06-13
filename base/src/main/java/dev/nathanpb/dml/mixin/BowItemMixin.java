package dev.nathanpb.dml.mixin;
/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

import dev.nathanpb.dml.armor.modular.effects.ArcheryEffect;
import dev.nathanpb.safer.Safer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BowItem.class)
public abstract class BowItemMixin {

    @Shadow
    public static float getPullProgress(int useTicks) {
        return 0;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"), method = "onStoppedUsing")
    public float proxyPullProgress(int useTicks, ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return Safer.runLazy(() -> getPullProgress(useTicks), () -> {
            if (user instanceof PlayerEntity) {
                float modifier = ArcheryEffect.Companion.bowFastpullLevels((PlayerEntity) user) + 1;
                if (modifier > 1) {
                    float f = (float)useTicks / (20F / modifier);
                    f = (f * f + f * 2.0F) / 3.0F;
                    if (f > 1.0F) {
                        f = 1.0F;
                    }

                    return f;
                }
            }
            return getPullProgress(useTicks);
        });
    }
}
