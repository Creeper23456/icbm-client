// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.state;

import cn.cubegarden.icbm.client.protocol.StateSnapshotPayload;
import java.util.Optional;

/** Main-thread cache for the most recent complete server snapshot. */
public final class IcbmClientState {
    public static final IcbmClientState INSTANCE = new IcbmClientState();
    private static final int MIN_REFRESH_INTERVAL_TICKS = 20;
    private static final int MAX_REFRESH_INTERVAL_TICKS = 1200;

    private StateSnapshotPayload snapshot;
    private long sequence = Long.MIN_VALUE;
    private long expiresAtNanos;

    private IcbmClientState() {
    }

    public void accept(StateSnapshotPayload incoming) {
        if (incoming.sequence() < sequence) {
            return;
        }
        snapshot = incoming;
        sequence = incoming.sequence();
        expiresAtNanos = System.nanoTime() + expiryNanos(incoming.refreshIntervalTicks());
    }

    public Optional<StateSnapshotPayload> current() {
        if (snapshot == null || System.nanoTime() >= expiresAtNanos) {
            clear();
            return Optional.empty();
        }
        return Optional.of(snapshot);
    }

    public void clear() {
        snapshot = null;
        sequence = Long.MIN_VALUE;
        expiresAtNanos = 0L;
    }

    static long expiryNanos(int refreshIntervalTicks) {
        int boundedTicks = Math.clamp(refreshIntervalTicks, MIN_REFRESH_INTERVAL_TICKS, MAX_REFRESH_INTERVAL_TICKS);
        return boundedTicks * 2L * 50_000_000L;
    }
}
