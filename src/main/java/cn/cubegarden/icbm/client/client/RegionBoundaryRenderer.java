// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.client;

import cn.cubegarden.icbm.client.IcbmClient;
import cn.cubegarden.icbm.client.protocol.RegionBoundary;
import cn.cubegarden.icbm.client.protocol.StateSnapshotPayload;
import cn.cubegarden.icbm.client.state.IcbmClientState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/** Draws the server-authoritative, clipped Region boundary as a red line box. */
@EventBusSubscriber(modid = IcbmClient.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class RegionBoundaryRenderer {
    private RegionBoundaryRenderer() {
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.getDebugOverlay().showDebugScreen() || minecraft.level == null) {
            return;
        }

        Optional<StateSnapshotPayload> current = IcbmClientState.INSTANCE.current();
        if (current.isEmpty()) {
            return;
        }
        StateSnapshotPayload snapshot = current.get();
        if (!snapshot.simulationClipped() || !snapshot.dimension().equals(minecraft.level.dimension().location()) || snapshot.region().isEmpty()) {
            return;
        }

        RegionBoundary boundary = snapshot.region().get();
        AABB box = new AABB(boundary.minX(), minecraft.level.getMinBuildHeight(), boundary.minZ(), boundary.maxX(), minecraft.level.getMaxBuildHeight(), boundary.maxZ());
        if (!event.getFrustum().isVisible(box)) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        event.getPoseStack().pushPose();
        event.getPoseStack().translate(-camera.x, -camera.y, -camera.z);
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(event.getPoseStack(), lines, box, 1.0F, 0.0F, 0.0F, 1.0F);
        buffers.endBatch(RenderType.lines());
        event.getPoseStack().popPose();
    }
}
