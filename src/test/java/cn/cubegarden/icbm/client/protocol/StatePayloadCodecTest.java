package cn.cubegarden.icbm.client.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.netty.buffer.Unpooled;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

class StatePayloadCodecTest {
    @Test
    void snapshotRoundTripsEveryOptionalField() {
        StateSnapshotPayload original = new StateSnapshotPayload(
                1,
                42L,
                ResourceLocation.withDefaultNamespace("overworld"),
                true,
                Optional.of(new RegionBoundary(-16, -32, 48, 32)),
                Optional.of("region-alpha"),
                Optional.of("worker-3"),
                Optional.of(12.5D),
                Optional.of(19.75F),
                20);

        RegistryFriendlyByteBuf buffer = buffer();
        StateSnapshotPayload.STREAM_CODEC.encode(buffer, original);
        assertEquals(original, StateSnapshotPayload.STREAM_CODEC.decode(buffer));
    }

    @Test
    void subscriptionRoundTrips() {
        StateSubscriptionPayload original = new StateSubscriptionPayload(1, true);
        RegistryFriendlyByteBuf buffer = buffer();
        StateSubscriptionPayload.STREAM_CODEC.encode(buffer, original);
        assertEquals(original, StateSubscriptionPayload.STREAM_CODEC.decode(buffer));
    }

    private static RegistryFriendlyByteBuf buffer() {
        return new RegistryFriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()), RegistryAccess.EMPTY);
    }
}
