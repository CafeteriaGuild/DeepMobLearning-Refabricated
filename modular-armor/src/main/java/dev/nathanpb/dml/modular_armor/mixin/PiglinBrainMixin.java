package dev.nathanpb.dml.modular_armor.mixin;

import dev.nathanpb.dml.modular_armor.effects.PiglinTruceEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    @Inject(method = "wearsGoldArmor", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void wearsGoldArmor(LivingEntity entity, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity) {
            if (!cir.getReturnValue()) {
                ActionResult result = PiglinTruceEffect.Companion.trigger((PlayerEntity) entity);

                if(result == ActionResult.FAIL) {
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        }
    }

}