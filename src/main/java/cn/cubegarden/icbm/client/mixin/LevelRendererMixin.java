// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.mixin;

import cn.cubegarden.icbm.client.client.RegionWorldBorderRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Supplies the ICBM visual Region border to Minecraft's own border renderer. */
@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin {
    @Redirect(
            method = "renderWorldBorder",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"
            )
    )
    private WorldBorder icbmClient$selectRegionWorldBorder(ClientLevel level) {
        return RegionWorldBorderRenderer.select(level, level.getWorldBorder());
    }
}
