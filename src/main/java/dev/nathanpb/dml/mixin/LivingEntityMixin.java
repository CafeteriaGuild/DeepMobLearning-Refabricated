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
import dev.nathanpb.dml.entity.SystemGlitchEntity;
import dev.nathanpb.dml.entity.effect.StatusEffectsKt;
import dev.nathanpb.dml.event.LivingEntityDieCallback;
import dev.nathanpb.dml.event.context.EventsKt;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ILivingEntityReiStateAccessor  {
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
        LivingEntity dis = (LivingEntity) (Object) this;
        if (dis.hasStatusEffect(StatusEffectsKt.getDEPTH_STRIDER_EFFECT())) {
            return value + dis.getStatusEffect(StatusEffectsKt.getDEPTH_STRIDER_EFFECT()).getAmplifier();
        }
        return value;
    }

    // TODO use ModifyVar as smart guys do
    @Inject(at = @At("RETURN"), method = "tryUseTotem", cancellable = true)
    public void undyingEffect(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            LivingEntity dis = (LivingEntity) (Object) this;
            if (dis instanceof PlayerEntity) {
                ItemStack totemStack = EventsKt.getFindTotemOfUndyingCallback()
                        .invoker()
                        .invoke((PlayerEntity) dis);

                if (totemStack != null) {
                    dis.setHealth(1.0F);
                    dis.clearStatusEffects();
                    dis.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                    dis.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                    dis.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                    dis.world.sendEntityStatus(dis, (byte)35);

                    if (dis instanceof ServerPlayerEntity) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)dis;
                        serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
                        Criteria.USED_TOTEM.trigger(serverPlayerEntity, totemStack);
                    }
                    totemStack.decrement(1);
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true)
    public void canTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity dis = (LivingEntity) (Object) this;
        if (dis instanceof MobEntity) {
            if (cir.getReturnValue()) {
                ActionResult eventResult = EventsKt.getCanTargetEntityEvent()
                    .invoker()
                    .invoke((MobEntity) dis, target);

                if (eventResult.equals(ActionResult.FAIL)) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }

    @ModifyVariable(
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/FoodComponent;getStatusEffects()Ljava/util/List;"),
        method = "applyFoodEffects"
    )
    public List<Pair<StatusEffectInstance, Float>> applyFoodEffects(List<Pair<StatusEffectInstance, Float>> effects, ItemStack stack) {
        return EventsKt.getFoodStatusEffectsCallback()
            .invoker()
            .invoke((LivingEntity) (Object) this, stack, effects);
    }

    @Inject(at = @At("RETURN"), method = "eatFood")
    public void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (stack.isFood()) {
            EventsKt.getLivingEntityEatEvent()
                .invoker()
                .invoke((LivingEntity) (Object) this, stack);
        }
    }
}
