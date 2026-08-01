// SPDX-License-Identifier: GPL-3.0-only
package cn.cubegarden.icbm.client.protocol;

import cn.cubegarden.icbm.client.IcbmClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client request to start or stop receiving ICBM state snapshots. */
public record StateSubscriptionPayload(int protocolVersion, boolean enabled) implements CustomPacketPayload {
    public static final Type<StateSubscriptionPayload> TYPE = new Type<>(IcbmClient.id("state_subscription"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StateSubscriptionPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StateSubscriptionPayload decode(RegistryFriendlyByteBuf buffer) {
            return new StateSubscriptionPayload(buffer.readVarInt(), buffer.readBoolean());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, StateSubscriptionPayload payload) {
            buffer.writeVarInt(payload.protocolVersion);
            buffer.writeBoolean(payload.enabled);
        }
    };

    @Override
    public Type<StateSubscriptionPayload> type() {
        return TYPE;
    }
}
