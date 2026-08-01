// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.protocol;

import cn.cubegarden.icbm.client.IcbmClient;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Complete ICBM state snapshot sent by a subscribed server. */
public record StateSnapshotPayload(
        int protocolVersion,
        long sequence,
        ResourceLocation dimension,
        boolean simulationClipped,
        Optional<RegionBoundary> region,
        Optional<String> regionId,
        Optional<String> processId,
        Optional<Double> speedMetersPerSecond,
        Optional<Float> serverTps,
        int refreshIntervalTicks) implements CustomPacketPayload {
    public static final Type<StateSnapshotPayload> TYPE = new Type<>(IcbmClient.id("state_snapshot"));
    private static final int MAX_IDENTIFIER_LENGTH = 256;

    public static final StreamCodec<RegistryFriendlyByteBuf, StateSnapshotPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StateSnapshotPayload decode(RegistryFriendlyByteBuf buffer) {
            int protocolVersion = buffer.readVarInt();
            long sequence = buffer.readVarLong();
            ResourceLocation dimension = buffer.readResourceLocation();
            boolean simulationClipped = buffer.readBoolean();
            Optional<RegionBoundary> region = readRegion(buffer);
            Optional<String> regionId = readOptionalString(buffer);
            Optional<String> processId = readOptionalString(buffer);
            Optional<Double> speed = readOptionalDouble(buffer);
            Optional<Float> tps = readOptionalFloat(buffer);
            int refreshIntervalTicks = buffer.readVarInt();
            return new StateSnapshotPayload(protocolVersion, sequence, dimension, simulationClipped, region, regionId, processId, speed, tps, refreshIntervalTicks);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, StateSnapshotPayload payload) {
            buffer.writeVarInt(payload.protocolVersion);
            buffer.writeVarLong(payload.sequence);
            buffer.writeResourceLocation(payload.dimension);
            buffer.writeBoolean(payload.simulationClipped);
            writeRegion(buffer, payload.region);
            writeOptionalString(buffer, payload.regionId);
            writeOptionalString(buffer, payload.processId);
            writeOptionalDouble(buffer, payload.speedMetersPerSecond);
            writeOptionalFloat(buffer, payload.serverTps);
            buffer.writeVarInt(payload.refreshIntervalTicks);
        }
    };

    public StateSnapshotPayload {
        Objects.requireNonNull(dimension, "dimension");
        region = region == null ? Optional.empty() : region;
        regionId = regionId == null ? Optional.empty() : regionId;
        processId = processId == null ? Optional.empty() : processId;
        speedMetersPerSecond = speedMetersPerSecond == null ? Optional.empty() : speedMetersPerSecond;
        serverTps = serverTps == null ? Optional.empty() : serverTps;
        regionId.ifPresent(StateSnapshotPayload::validateIdentifier);
        processId.ifPresent(StateSnapshotPayload::validateIdentifier);
    }

    @Override
    public Type<StateSnapshotPayload> type() {
        return TYPE;
    }

    private static Optional<RegionBoundary> readRegion(RegistryFriendlyByteBuf buffer) {
        if (!buffer.readBoolean()) {
            return Optional.empty();
        }
        return Optional.of(new RegionBoundary(buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt()));
    }

    private static void writeRegion(RegistryFriendlyByteBuf buffer, Optional<RegionBoundary> region) {
        buffer.writeBoolean(region.isPresent());
        region.ifPresent(value -> {
            buffer.writeVarInt(value.minX());
            buffer.writeVarInt(value.minZ());
            buffer.writeVarInt(value.maxX());
            buffer.writeVarInt(value.maxZ());
        });
    }

    private static Optional<String> readOptionalString(RegistryFriendlyByteBuf buffer) {
        return buffer.readBoolean() ? Optional.of(buffer.readUtf(MAX_IDENTIFIER_LENGTH)) : Optional.empty();
    }

    private static void writeOptionalString(RegistryFriendlyByteBuf buffer, Optional<String> value) {
        buffer.writeBoolean(value.isPresent());
        value.ifPresent(item -> buffer.writeUtf(item, MAX_IDENTIFIER_LENGTH));
    }

    private static Optional<Double> readOptionalDouble(RegistryFriendlyByteBuf buffer) {
        return buffer.readBoolean() ? Optional.of(buffer.readDouble()) : Optional.empty();
    }

    private static void writeOptionalDouble(RegistryFriendlyByteBuf buffer, Optional<Double> value) {
        buffer.writeBoolean(value.isPresent());
        value.ifPresent(buffer::writeDouble);
    }

    private static Optional<Float> readOptionalFloat(RegistryFriendlyByteBuf buffer) {
        return buffer.readBoolean() ? Optional.of(buffer.readFloat()) : Optional.empty();
    }

    private static void writeOptionalFloat(RegistryFriendlyByteBuf buffer, Optional<Float> value) {
        buffer.writeBoolean(value.isPresent());
        value.ifPresent(buffer::writeFloat);
    }

    private static void validateIdentifier(String value) {
        if (value.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > MAX_IDENTIFIER_LENGTH) {
            throw new IllegalArgumentException("ICBM identifier exceeds %d UTF-8 bytes".formatted(MAX_IDENTIFIER_LENGTH));
        }
    }
}
