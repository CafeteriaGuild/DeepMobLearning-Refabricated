package dev.nathanpb.dml.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityType.class)
public interface EntityTypeMixin {

    @Invoker("newInstance")
    static Entity invokeNewInstance(World world, @Nullable EntityType<?> type) {
        throw new AssertionError();
    }
}
