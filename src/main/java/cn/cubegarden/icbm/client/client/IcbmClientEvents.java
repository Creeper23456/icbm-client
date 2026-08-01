// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.client;

import cn.cubegarden.icbm.client.IcbmClient;
import cn.cubegarden.icbm.client.protocol.IcbmPayloads;
import cn.cubegarden.icbm.client.protocol.StateSnapshotPayload;
import cn.cubegarden.icbm.client.protocol.StateSubscriptionPayload;
import cn.cubegarden.icbm.client.state.IcbmClientState;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

/** F3 diagnostics and subscription lifecycle. */
@EventBusSubscriber(modid = IcbmClient.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class IcbmClientEvents {
    private static boolean debugWasVisible;

    private IcbmClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean debugVisible = minecraft.getDebugOverlay().showDebugScreen();
        if (debugVisible != debugWasVisible) {
            sendSubscription(debugVisible);
            debugWasVisible = debugVisible;
        }
        IcbmClientState.INSTANCE.current();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (debugWasVisible) {
            sendSubscription(false);
        }
        debugWasVisible = false;
        IcbmClientState.INSTANCE.clear();
    }

    @SubscribeEvent
    public static void addDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.getDebugOverlay().showDebugScreen() || minecraft.level == null) {
            return;
        }

        Optional<StateSnapshotPayload> current = IcbmClientState.INSTANCE.current();
        if (current.isEmpty() || !current.get().dimension().equals(minecraft.level.dimension().location())) {
            return;
        }

        StateSnapshotPayload snapshot = current.get();
        snapshot.speedMetersPerSecond().ifPresent(speed -> event.getLeft().add("ICBM Speed: %.2f m/s".formatted(speed)));
        snapshot.regionId().or(() -> snapshot.processId()).ifPresent(id -> event.getLeft().add("ICBM Region: " + id));
        snapshot.serverTps().ifPresent(tps -> event.getLeft().add("ICBM TPS: %.2f".formatted(tps)));
    }

    private static void sendSubscription(boolean enabled) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();
        if (connection == null || !NetworkRegistry.hasChannel(connection, StateSubscriptionPayload.TYPE.id())) {
            return;
        }
        PacketDistributor.sendToServer(new StateSubscriptionPayload(IcbmPayloads.PROTOCOL_VERSION, enabled));
    }
}
