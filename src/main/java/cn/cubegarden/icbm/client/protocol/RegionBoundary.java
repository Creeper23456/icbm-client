// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.protocol;

/** A horizontal Region rectangle in block-boundary coordinates. */
public record RegionBoundary(int minX, int minZ, int maxX, int maxZ) {
    public RegionBoundary {
        if (minX >= maxX || minZ >= maxZ) {
            throw new IllegalArgumentException("Region maximum coordinates must be greater than minimum coordinates");
        }
    }
}
