package dev.nathanpb.dml.mixin;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Supplier;

@Mixin(Registry.class)
public interface RegistryInvoker {
    @Invoker
    static <T, R extends MutableRegistry<T>> R invokeCreate(RegistryKey<? extends Registry<T>> key, R registry, Supplier<T> defaultEntry, Lifecycle lifecycle) {
        return null;
    }
}
