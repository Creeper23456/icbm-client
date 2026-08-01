// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.client;

import cn.cubegarden.icbm.client.protocol.StateSnapshotPayload;
import cn.cubegarden.icbm.client.state.IcbmClientState;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.border.WorldBorder;

/** Selects the temporary Region border for vanilla rendering without changing the level border. */
public final class RegionWorldBorderRenderer {
    private static final RegionWorldBorder REGION_BORDER = new RegionWorldBorder();

    private RegionWorldBorderRenderer() {
    }

    public static WorldBorder select(ClientLevel level, WorldBorder vanillaWorldBorder) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.getDebugOverlay().showDebugScreen()) {
            return vanillaWorldBorder;
        }

        Optional<StateSnapshotPayload> current = IcbmClientState.INSTANCE.current();
        if (current.isEmpty()) {
            return vanillaWorldBorder;
        }

        StateSnapshotPayload snapshot = current.get();
        if (!snapshot.simulationClipped() || !snapshot.dimension().equals(level.dimension().location()) || snapshot.region().isEmpty()) {
            return vanillaWorldBorder;
        }

        REGION_BORDER.setBoundary(snapshot.region().get());
        return REGION_BORDER;
    }
}
