package cn.cubegarden.icbm.client.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class IcbmClientStateTest {
    @Test
    void usesTwoRefreshPeriodsAndClampsServerIntervals() {
        assertEquals(2_000_000_000L, IcbmClientState.expiryNanos(20));
        assertEquals(2_000_000_000L, IcbmClientState.expiryNanos(1));
        assertEquals(120_000_000_000L, IcbmClientState.expiryNanos(10_000));
    }
}
