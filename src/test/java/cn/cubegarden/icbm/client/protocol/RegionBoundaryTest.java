package cn.cubegarden.icbm.client.protocol;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class RegionBoundaryTest {
    @Test
    void acceptsPositiveArea() {
        assertDoesNotThrow(() -> new RegionBoundary(-16, -32, 16, 32));
    }

    @Test
    void rejectsEmptyOrReversedBounds() {
        assertThrows(IllegalArgumentException.class, () -> new RegionBoundary(0, 0, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new RegionBoundary(0, 4, 1, 4));
    }
}
