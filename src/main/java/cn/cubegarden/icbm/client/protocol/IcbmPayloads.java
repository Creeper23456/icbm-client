// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.protocol;

import cn.cubegarden.icbm.client.IcbmClient;
import cn.cubegarden.icbm.client.state.IcbmClientState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Registers the optional client half of the ICBM protocol. */
@EventBusSubscriber(modid = IcbmClient.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class IcbmPayloads {
    public static final int PROTOCOL_VERSION = 1;
    public static final String NETWORK_VERSION = "1";

    private IcbmPayloads() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION).optional();
        registrar.playToClient(StateSnapshotPayload.TYPE, StateSnapshotPayload.STREAM_CODEC, IcbmPayloads::handleSnapshot);
        registrar.playToServer(StateSubscriptionPayload.TYPE, StateSubscriptionPayload.STREAM_CODEC, IcbmPayloads::ignoreClientSubscription);
    }

    private static void handleSnapshot(StateSnapshotPayload payload, IPayloadContext context) {
        if (payload.protocolVersion() == PROTOCOL_VERSION) {
            context.enqueueWork(() -> IcbmClientState.INSTANCE.accept(payload));
        } else {
            IcbmClient.LOGGER.debug("Ignoring ICBM state snapshot with unsupported protocol version {}", payload.protocolVersion());
        }
    }

    private static void ignoreClientSubscription(StateSubscriptionPayload payload, IPayloadContext context) {
        // A dedicated ICBM server supplies the server handler. This only makes integrated-client development safe.
    }
}
