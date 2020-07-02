package dev.nathanpb.dml.net

import dev.nathanpb.dml.identifier
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

val TRIAL_ENDED_PACKET = identifier("trial_ended")
val TRIAL_UPDATED_PACKET = identifier("trial_updated")

fun registerClientSidePackets() {
    hashMapOf(
        TRIAL_ENDED_PACKET to TrialEndedPacket.CONSUMER,
        TRIAL_UPDATED_PACKET to TrialUpdatedPacket.CONSUMER
    ).forEach { (id, consumer) ->
        ClientSidePacketRegistry.INSTANCE.register(id, consumer)
    }
}
