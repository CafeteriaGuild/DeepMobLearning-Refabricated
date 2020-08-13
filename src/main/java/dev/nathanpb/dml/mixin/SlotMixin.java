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

import dev.nathanpb.dml.data.TrialKeyData;
import dev.nathanpb.dml.data.TrialKeyDataKt;
import dev.nathanpb.dml.item.ItemTrialKey;
import dev.nathanpb.dml.trial.affix.UtilsKt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(at = @At("RETURN"), method = "takeStack")
    public void onTake(int amount, CallbackInfoReturnable<ItemStack> ci) {
        ItemStack stack = ci.getReturnValue();
        Slot dis = (Slot) (Object) this;
        if (dis instanceof CraftingResultSlot && stack != null && stack.getItem() instanceof ItemTrialKey) {
            PlayerEntity player = ((ICraftingResultSlotMixin)dis).dmlRefGetPlayer();
            if (!player.world.isClient) {
                TrialKeyData oldData = TrialKeyDataKt.getTrialKeyData(stack);
                if (oldData != null && oldData.getAffixes().isEmpty()) {
                    TrialKeyData newData = new TrialKeyData(stack);
                    newData.setAffixes(UtilsKt.pickRandomAffixes());
                }
            }
        }
    }
}
