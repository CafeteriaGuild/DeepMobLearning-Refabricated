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

import com.mojang.datafixers.util.Pair;
import dev.nathanpb.dml.accessor.ILivingEntityReiStateAccessor;
import dev.nathanpb.dml.armor.modular.TargetCancellationEffect;
import dev.nathanpb.dml.armor.modular.effects.RotResistanceEffect;
import dev.nathanpb.dml.armor.modular.effects.UndyingEffect;
import dev.nathanpb.dml.entity.SystemGlitchEntity;
import dev.nathanpb.dml.entity.effect.StatusEffectsKt;
import dev.nathanpb.dml.event.context.EventsKt;
import dev.nathanpb.dml.event.context.LivingEntityDamageContext;
import dev.nathanpb.dml.item.ItemModularGlitchArmor;
import dev.nathanpb.safer.Safer;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.StreamSupport;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ILivingEntityReiStateAccessor  {
    boolean dmlRefIsInREIScreen = false;

    @Inject(at = @At("HEAD"), method = "getMaxHealth", cancellable = true)
    public void getMaxHealth(CallbackInfoReturnable<Float> cir) {
        Safer.run(() -> {
            if ((Object) this instanceof SystemGlitchEntity) {
                SystemGlitchEntity dis = (SystemGlitchEntity) ((Object)this);
                if (dis.getTier() != null) {
                    float health = dis.getTier().getSystemGlitchMaxHealth();
                    cir.setReturnValue(health);
                    cir.cancel();
                }
            }
        });
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"), method = "onEquipStack", cancellable = true)
    public void onEquip(ItemStack stack, CallbackInfo ci) {
        if (isDmlRefIsInReiScreen()) {
            ci.cancel();
        }
    }

    @Override
    public boolean isDmlRefIsInReiScreen() {
        return dmlRefIsInREIScreen;
    }

    @Override
    public void setDmlRefInReiScreen(boolean flag) {
        dmlRefIsInREIScreen = flag;
    }

    @ModifyVariable(
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;onGround:Z"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getBaseMovementSpeedMultiplier()F", ordinal = 0),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;onGround:Z", ordinal = 0)
            ),
            method = "travel",
            ordinal = 2
    )
    public float depthStriderEffectTravelPath(float value) {
        return Safer.run(value, () -> {
            LivingEntity dis = (LivingEntity) (Object) this;
            if (dis.hasStatusEffect(StatusEffectsKt.getDEPTH_STRIDER_EFFECT())) {
                return value + dis.getStatusEffect(StatusEffectsKt.getDEPTH_STRIDER_EFFECT()).getAmplifier();
            }
            return value;
        });
    }

    // TODO use ModifyVar as smart guys do
    @Inject(at = @At("RETURN"), method = "tryUseTotem", cancellable = true)
    public void undyingEffect(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
       Safer.run(() -> {
           if (!cir.getReturnValue()) {
               LivingEntity dis = (LivingEntity) (Object) this;
               if (dis instanceof PlayerEntity) {
                   if (UndyingEffect.Companion.trigger((PlayerEntity) dis)) {
                       cir.setReturnValue(true);
                       cir.cancel();
                   }
               }
           }
       });
    }

    @Inject(at = @At("RETURN"), method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true)
    public void canTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        Safer.run(() -> {
            LivingEntity dis = (LivingEntity) (Object) this;
            if (dis instanceof MobEntity) {
                if (cir.getReturnValue()) {
                    ActionResult result = TargetCancellationEffect.Companion.attemptToCancel((MobEntity) dis, target);
                    if (result.equals(ActionResult.FAIL)) {
                        cir.setReturnValue(false);
                        cir.cancel();
                    }
                }
            }
        });
    }

    @ModifyVariable(
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/FoodComponent;getStatusEffects()Ljava/util/List;"),
        method = "applyFoodEffects"
    )
    public List<Pair<StatusEffectInstance, Float>> applyFoodEffects(List<Pair<StatusEffectInstance, Float>> effects, ItemStack stack) {
        return Safer.run(effects, () -> RotResistanceEffect.Companion.attemptToCancelHunger((LivingEntity) (Object) this, stack, effects));
    }

    @Inject(at = @At("RETURN"), method = "eatFood")
    public void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        Safer.run(() -> {
            if (stack.isFood()) {
                EventsKt.getLivingEntityEatEvent()
                        .invoker()
                        .invoke((LivingEntity) (Object) this, stack);
            }
        });
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(FFF)F"), method = "applyArmorToDamage")
    public float glitchArmorUncapProtection(float damage, float armor, float armorToughness, DamageSource source, float damage2) {
        return Safer.runLazy(() -> DamageUtil.getDamageLeft(damage, armor, armorToughness), () -> {
            boolean shouldUncap = StreamSupport.stream(((LivingEntity) (Object) this).getArmorItems().spliterator(), false)
                    .anyMatch(it -> it.getItem() instanceof ItemModularGlitchArmor);
            if (shouldUncap) {
                float f = 2.0F + armorToughness / 4.0F;
                float g = Math.max(armor - damage / f, armor * 0.2F);
                return damage * (1.0F - g / 25.0F);
            } else {
                return DamageUtil.getDamageLeft(damage, armor, armorToughness);
            }
        });
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), method = "applyDamage")
    private float applyDamage(DamageSource source, float amount) {
        return Safer.run(amount, () -> {
            LivingEntity dis = (LivingEntity) (Object) this;
            return EventsKt.getLivingEntityDamageEvent()
                    .invoker()
                    .invoke(new LivingEntityDamageContext(dis, source, amount))
                    .getDamage();
        });
    }
}
