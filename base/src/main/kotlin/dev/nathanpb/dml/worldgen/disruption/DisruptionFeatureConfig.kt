package dev.nathanpb.dml.worldgen.disruption

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.math.intprovider.IntProvider
import net.minecraft.world.gen.feature.FeatureConfig


class DisruptionFeatureConfig(val radius: IntProvider) : FeatureConfig {

    companion object {
        val CODEC: Codec<DisruptionFeatureConfig> =
            RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<DisruptionFeatureConfig> ->
                instance.group(
                    IntProvider.createValidatingCodec(0, 8).fieldOf("radius").forGetter(DisruptionFeatureConfig::radius),
                ).apply(instance, ::DisruptionFeatureConfig)
        }
    }
}