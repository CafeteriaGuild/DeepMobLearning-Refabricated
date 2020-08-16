package dev.nathanpb.dml.mixin;

/*
 * Copyright (C) 2020 Nathan P. Bombana, IterationFunk
 *
 * This file is part of Deep Mob Learning: Refabricated.
 *
 * Deep Mob Learning: Refabricated is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Deep Mob Learning: Refabricated is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Deep Mob Learning: Refabricated.  If not, see <https://www.gnu.org/licenses/>.
 */

import dev.nathanpb.dml.accessor.ILivingEntityReiStateAccessor;
import dev.nathanpb.dml.entity.SystemGlitchEntity;
import dev.nathanpb.dml.event.LivingEntityDieCallback;
import dev.nathanpb.dml.event.context.EventsKt;
import dev.nathanpb.dml.event.context.LivingEntityDamageContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntityReiStateAccessor  {
    boolean dmlRefIsInREIScreen = false;

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

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"), method = "onEquipStack", cancellable = true)
    public void onEquip(ItemStack stack, CallbackInfo ci) {
        if (isDmlRefIsInReiScreen()) {
            ci.cancel();
        }
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyEnchantmentsToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), method = "applyDamage")
    private float applyDamage(DamageSource source, float amount) {
        LivingEntity dis = (LivingEntity) (Object) this;
        return EventsKt.getLivingEntityDamageEvent()
            .invoker()
            .invoke(new LivingEntityDamageContext(dis, source, amount))
            .getDamage();
    }

    @Override
    public boolean isDmlRefIsInReiScreen() {
        return dmlRefIsInREIScreen;
    }

    @Override
    public void setDmlRefInReiScreen(boolean flag) {
        dmlRefIsInREIScreen = flag;
    }
}
