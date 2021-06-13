package dev.nathanpb.dml.mixin;/*
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
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    @ModifyVariable(at = @At("INVOKE"), ordinal = 0, method = "usageTick")
    public int modifyRemainingTicks(int remainingTicks, World world, LivingEntity user, ItemStack stack, int ignored) {
        return Safer.run(remainingTicks, () -> {
            if (user instanceof PlayerEntity) {
                return Math.max(0, remainingTicks - ArcheryEffect.Companion.crossbowFastpullReducedTicks((PlayerEntity) user));
            } else {
                return remainingTicks;
            }
        });
    }

    @ModifyVariable(at = @At("INVOKE"), ordinal = 0, method = "onStoppedUsing")
    public int modifyRemainingTicks2(int remainingTicks, ItemStack stack, World world, LivingEntity user, int ignored) {
        return Safer.run(remainingTicks, () -> {
            if (user instanceof PlayerEntity) {
                return Math.max(0, remainingTicks - ArcheryEffect.Companion.crossbowFastpullReducedTicks((PlayerEntity) user));
            } else {
                return remainingTicks;
            }
        });
    }
}
