// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.client;

import cn.cubegarden.icbm.client.protocol.RegionBoundary;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class RegionWorldBorderTest {
    @Test
    void configuresVanillaBorderForSquareRegion() {
        RegionWorldBorder border = new RegionWorldBorder();
        border.setBoundary(new RegionBoundary(-16, -32, 48, 32));

        assertEquals(-16.0, border.getMinX());
        assertEquals(-32.0, border.getMinZ());
        assertEquals(48.0, border.getMaxX());
        assertEquals(32.0, border.getMaxZ());
        assertEquals(16.0, border.getCenterX());
        assertEquals(0.0, border.getCenterZ());
        assertEquals(64.0, border.getSize());
    }
}
