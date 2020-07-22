package dev.nathanpb.dml.net

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketConsumer
import net.minecraft.util.Identifier


fun registerClientSidePackets() {
    hashMapOf<Identifier, PacketConsumer>(
        // I'll be keeping this here to use when I need packets again
    ).forEach { (id, consumer) ->
        ClientSidePacketRegistry.INSTANCE.register(id, consumer)
    }
}
