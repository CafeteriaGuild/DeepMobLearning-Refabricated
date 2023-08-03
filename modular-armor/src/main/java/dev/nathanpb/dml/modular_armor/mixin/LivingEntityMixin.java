package dev.nathanpb.dml.modular_armor.mixin;

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

import dev.nathanpb.dml.item.ItemEmeritusHat;
import dev.nathanpb.dml.modular_armor.ItemModularGlitchArmor;
import dev.nathanpb.dml.modular_armor.effects.RotResistanceEffect;
import dev.nathanpb.dml.modular_armor.effects.TargetCancellationEffect;
import dev.nathanpb.dml.modular_armor.effects.UndyingEffect;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.StreamSupport;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    // FIXME
    /*@ModifyVariable(
            method = "travel",
            at = @At(
                    target = "Lnet/minecraft/entity/LivingEntity;onGround:Z",
                    value = "FIELD"
            ),
            slice = @Slice(
                    from = @At(
                            target = "Lnet/minecraft/entity/LivingEntity;getBaseMovementSpeedMultiplier()F",
                            value = "INVOKE",
                            ordinal = 0
                    ),
                    to = @At(
                            target = "Lnet/minecraft/entity/LivingEntity;onGround:Z",
                            value = "FIELD",
                            ordinal = 0
                    )
            ),
            ordinal = 2
    )
    public float depthStriderEffectTravelPath(float value) {
        LivingEntity dis = (LivingEntity) (Object) this;
        if (dis.hasStatusEffect(EntityStatusEffectsKt.getDEPTH_STRIDER_EFFECT())) {
            return value + dis.getStatusEffect(EntityStatusEffectsKt.getDEPTH_STRIDER_EFFECT()).getAmplifier();
        }
        return value;
    }*/

    // TODO use ModifyVar as smart guys do
    @Inject(at = @At("RETURN"), method = "tryUseTotem", cancellable = true)
    public void undyingEffect(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            LivingEntity dis = (LivingEntity) (Object) this;
            if (dis instanceof PlayerEntity) {
                if (UndyingEffect.Companion.trigger((PlayerEntity) dis)) {
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true)
    public void canTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
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
    }

    @ModifyVariable(
        method = "applyFoodEffects",
        at = @At(
            target = "Lnet/minecraft/item/FoodComponent;getStatusEffects()Ljava/util/List;",
            value = "STORE"
        )
    )
    public List<Pair<StatusEffectInstance, Float>> applyFoodEffects(List<Pair<StatusEffectInstance, Float>> effects, ItemStack stack) {
        return RotResistanceEffect.Companion
            .attemptToCancelHunger((LivingEntity) (Object) this, stack, effects);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(FFF)F"), method = "applyArmorToDamage")
    public float glitchArmorUncapProtection(float damage, float armor, float armorToughness, DamageSource source, float damage2) {
        boolean shouldUncap = StreamSupport.stream(((LivingEntity) (Object) this).getArmorItems().spliterator(), false)
            .anyMatch(it -> it.getItem() instanceof ItemModularGlitchArmor);

        if (shouldUncap) {
            float f = 2.0F + armorToughness / 4.0F;
            float g = Math.max(armor - damage / f, armor * 0.2F);
            return damage * (1.0F - g / 25.0F);
        } else {
            return DamageUtil.getDamageLeft(damage, armor, armorToughness);
        }
    }

    @Inject(at = @At("HEAD"), method = "getPreferredEquipmentSlot", cancellable = true)
    private static void getPreferredEquipmentSlot(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (stack.getItem() instanceof ItemEmeritusHat) {
            cir.setReturnValue(EquipmentSlot.HEAD);
            cir.cancel();
        }
    }
}
