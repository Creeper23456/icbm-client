// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.client;

import cn.cubegarden.icbm.client.protocol.RegionBoundary;
import net.minecraft.world.level.border.WorldBorder;

/** A temporary square Region border used exclusively by the vanilla world-border renderer. */
final class RegionWorldBorder extends WorldBorder {
    public void setBoundary(RegionBoundary boundary) {
        setCenter(
                ((double) boundary.minX() + boundary.maxX()) / 2.0,
                ((double) boundary.minZ() + boundary.maxZ()) / 2.0);
        setSize(boundary.maxX() - boundary.minX());
    }
}
