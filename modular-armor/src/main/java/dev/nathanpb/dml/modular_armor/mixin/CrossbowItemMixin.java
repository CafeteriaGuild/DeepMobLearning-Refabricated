package dev.nathanpb.dml.modular_armor.mixin;
/*
 *
 *  Copyright (C) 2021 Nathan P. Bombana, IterationFunk
 *
 *  This file is part of Deep Mob Learning: Refabricated.
 *
 *  Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

import dev.nathanpb.dml.modular_armor.effects.ArcheryEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    private LivingEntity user;

    @ModifyVariable(at = @At("INVOKE"), ordinal = 0, method = "usageTick")
    public int modifyRemainingTicks(int remainingTicks, World world, LivingEntity user, ItemStack stack, int ignored) {
        if (user instanceof PlayerEntity) {
            return Math.max(0, remainingTicks - ArcheryEffect.Companion.crossbowFastpullReducedTicks((PlayerEntity) user));
        } else {
            return remainingTicks;
        }
    }

    @ModifyArg(method = "onStoppedUsing", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/CrossbowItem;getPullProgress(ILnet/minecraft/item/ItemStack;)F"))
    public int modifyRemainingTicks2(int useTicks) {
        if (user instanceof PlayerEntity) {
            return useTicks + ArcheryEffect.Companion.crossbowFastpullReducedTicks((PlayerEntity) user);
        } else {
            return useTicks;
        }
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    public void livingEntityGetter(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        setUser(user);
    }

    private void setUser(LivingEntity user) {
        this.user = user;
    }
}
