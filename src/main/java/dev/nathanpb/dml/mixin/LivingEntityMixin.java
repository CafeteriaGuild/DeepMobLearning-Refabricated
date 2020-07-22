package dev.nathanpb.dml.mixin;

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see https://www.gnu.org/licenses/.
 */

import dev.nathanpb.dml.entity.SystemGlitchEntity;
import dev.nathanpb.dml.event.LivingEntityDieCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void heal(float amount);

    @Inject(at = @At("HEAD"), method = "onDeath")
    public void onDeath(DamageSource source, CallbackInfo ci) {
        LivingEntityDieCallback.EVENT.invoker().onDeath((LivingEntity) (Object) this, source);
    }

    @Inject(at = @At("HEAD"), method = "getMaxHealth", cancellable = true)
    public void getMaxHealth(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof SystemGlitchEntity) {
            SystemGlitchEntity dis = (SystemGlitchEntity) ((Object)this);
            if (dis.getTier() != null) {
                float health = dis.getTier().getSystemGlitchMaxHealth();
                cir.setReturnValue(health);
                cir.cancel();
            }
        }
    }
}
