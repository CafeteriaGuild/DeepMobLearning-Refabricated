package dev.nathanpb.dml.modular_armor.effects

import dev.nathanpb.dml.config
import dev.nathanpb.dml.enums.DataModelTier
import dev.nathanpb.dml.enums.EntityCategory
import dev.nathanpb.dml.identifier
import dev.nathanpb.dml.modular_armor.core.*
import dev.nathanpb.dml.utils.firstInstanceOrNull
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult

class PiglinTruceEffect : ModularEffect<ModularEffectTriggerPayload>(
    identifier("piglin_truce"),
    EntityCategory.NETHER,
    config.glitchArmor.costs::piglinTruce
) {

    companion object {
        private val INSTANCE by lazy {
            ModularEffectRegistry.INSTANCE.all.firstInstanceOrNull<PiglinTruceEffect>()
        }

        fun trigger(player: PlayerEntity): ActionResult = INSTANCE?.run {
            if (!player.world.isClient) {
                val contexts = ModularEffectContext.from(player)
                    .run(EffectStackOption.PRIORITIZE_GREATER.apply)

                if (player.world.time % 20 == 0L) {
                    contexts.any { context ->
                        attemptToApply(context, ModularEffectTriggerPayload.EMPTY) { _, _ -> }
                            .result == ActionResult.SUCCESS
                    }
                }

                contexts.any { context -> canApply(context, ModularEffectTriggerPayload.EMPTY) }
                    .let { canApply ->
                        if (canApply) {
                            return ActionResult.FAIL
                        }
                    }
            }
            null
        } ?: ActionResult.PASS

    }

    override fun registerEvents() { }

    override fun acceptTier(tier: DataModelTier): Boolean {
        return tier.ordinal >= 2
    }

}