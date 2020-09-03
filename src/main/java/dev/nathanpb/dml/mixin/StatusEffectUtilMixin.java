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

import dev.nathanpb.dml.entity.effect.StatusEffectsKt;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.tag.FluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectUtil.class)
public class StatusEffectUtilMixin {

    @Inject(at = @At("HEAD"), method = "hasHaste", cancellable = true)
    private static void underwaterHasteHasPatch(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (
            entity.hasStatusEffect(StatusEffectsKt.getUNDERWATER_HASTE_EFFECT())
            && entity.isSubmergedIn(FluidTags.WATER)
        ) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getHasteAmplifier", cancellable = true)
    private static void underwaterHasteAmplifierPatch(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (
            entity.hasStatusEffect(StatusEffectsKt.getUNDERWATER_HASTE_EFFECT())
            && entity.isSubmergedIn(FluidTags.WATER)
        ) {
            int underwaterHaste = entity.getStatusEffect(StatusEffectsKt.getUNDERWATER_HASTE_EFFECT()).getAmplifier();
            cir.setReturnValue(StatusEffectUtil.getHasteAmplifier(entity) + underwaterHaste);
            cir.cancel();
        }
    }



}
