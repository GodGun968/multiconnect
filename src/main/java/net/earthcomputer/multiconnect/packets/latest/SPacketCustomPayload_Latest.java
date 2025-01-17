package net.earthcomputer.multiconnect.packets.latest;

import net.earthcomputer.multiconnect.ap.Argument;
import net.earthcomputer.multiconnect.ap.FilledArgument;
import net.earthcomputer.multiconnect.ap.Handler;
import net.earthcomputer.multiconnect.ap.Length;
import net.earthcomputer.multiconnect.ap.MessageVariant;
import net.earthcomputer.multiconnect.ap.Polymorphic;
import net.earthcomputer.multiconnect.api.Protocols;
import net.earthcomputer.multiconnect.impl.ConnectionInfo;
import net.earthcomputer.multiconnect.packets.SPacketCustomPayload;
import net.earthcomputer.multiconnect.protocols.generic.CustomPayloadHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;

@MessageVariant(minVersion = Protocols.V1_14)
@Polymorphic
public abstract class SPacketCustomPayload_Latest implements SPacketCustomPayload {
    public ResourceLocation channel;

    @Polymorphic(stringValue = "brand")
    @MessageVariant(minVersion = Protocols.V1_14)
    public static class Brand extends SPacketCustomPayload_Latest implements SPacketCustomPayload.Brand {
        public String brand;
    }

    @Polymorphic(otherwise = true)
    @MessageVariant(minVersion = Protocols.V1_14)
    public static class Other extends SPacketCustomPayload_Latest implements SPacketCustomPayload.Other {
        @Length(remainingBytes = true)
        public byte[] data;

        @SuppressWarnings("deprecation")
        @Handler
        public static boolean handle(
                @Argument("channel") ResourceLocation channel,
                @Argument("data") byte[] data,
                @FilledArgument ClientPacketListener networkHandler
        ) {
            // call the deprecated method
            // below 1.12.2, we called the string version of the method already
            if (ConnectionInfo.protocolVersion > Protocols.V1_12_2) {
                CustomPayloadHandler.handleClientboundIdentifierCustomPayload(networkHandler, channel, data);
            }

            return CustomPayloadHandler.allowClientboundCustomPayload(channel);
        }
    }
}
