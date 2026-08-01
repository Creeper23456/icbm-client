// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/** Entry point for the optional ICBM client diagnostics mod. */
@Mod(value = IcbmClient.MOD_ID, dist = Dist.CLIENT)
public final class IcbmClient {
    public static final String MOD_ID = "icbm_client";
    public static final Logger LOGGER = LogUtils.getLogger();

    public IcbmClient() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
