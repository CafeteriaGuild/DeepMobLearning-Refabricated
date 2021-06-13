package dev.nathanpb.dml.modular_armor.mixin;/*
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

import com.google.common.collect.Multimap;
import dev.nathanpb.dml.data.DataModelData;
import dev.nathanpb.dml.modular_armor.ItemModularGlitchArmor;
import dev.nathanpb.dml.modular_armor.data.ModularArmorData;
import dev.nathanpb.safer.Safer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(at = @At("HEAD"), method = "isDamageable", cancellable = true)
    void isDamageable(CallbackInfoReturnable<Boolean> cir) {
        Safer.run(() -> {
            ItemStack dis = (ItemStack) (Object) this;
            if (dis.getItem() instanceof ItemModularGlitchArmor) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        });
    }

    @Inject(at = @At("HEAD"), method = "getAttributeModifiers", cancellable = true)
    void getAttributeModifiers(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        Safer.run(() -> {
            ItemStack dis = (ItemStack)(Object) this;
            if (dis.getItem() instanceof ItemModularGlitchArmor) {
                cir.setReturnValue(((ItemModularGlitchArmor)dis.getItem()).getAttributeModifiers(dis, equipmentSlot));
                cir.cancel();
            }
        });
    }

    @Inject(at = @At("HEAD"), method = "isDamaged", cancellable = true)
    void isDamaged(CallbackInfoReturnable<Boolean> cir) {
        Safer.run(() -> {
            ItemStack dis = (ItemStack)(Object) this;
            if (dis.getItem() instanceof ItemModularGlitchArmor) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        });
    }

    @Inject(at = @At("HEAD"), method = "getDamage", cancellable = true)
    void patchModularArmorDamage(CallbackInfoReturnable<Integer> cir) {
        Safer.run(() -> {
            ItemStack dis = (ItemStack) (Object) this;
            if (dis.getItem() instanceof ItemModularGlitchArmor) {
                ModularArmorData armor = new ModularArmorData(dis);
                DataModelData dataModel = armor.getDataModel();
                if (dataModel != null) {
                    cir.setReturnValue(armor.tier().nextTierOrCurrent().getDataAmount() - dataModel.getDataAmount());
                } else {
                    cir.setReturnValue(armor.tier().nextTierOrCurrent().getDataAmount()-1);
                }
                cir.cancel();
            }
        });
    }

    @Inject(at = @At("HEAD"), method = "getMaxDamage", cancellable = true)
    void patchModularArmorMaxDamage(CallbackInfoReturnable<Integer> cir) {
        Safer.run(() -> {
            ItemStack dis = (ItemStack) (Object) this;
            if (dis.getItem() instanceof ItemModularGlitchArmor) {
                cir.setReturnValue(new ModularArmorData(dis).tier().nextTierOrCurrent().getDataAmount()-1);
                cir.cancel();
            }
        });
    }
}
