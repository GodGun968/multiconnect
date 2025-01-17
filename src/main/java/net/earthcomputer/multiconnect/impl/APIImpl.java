package net.earthcomputer.multiconnect.impl;

import net.earthcomputer.multiconnect.api.*;
import net.earthcomputer.multiconnect.connect.ConnectionMode;
import net.earthcomputer.multiconnect.protocols.generic.CustomPayloadHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class APIImpl extends MultiConnectAPI {
    @Override
    public int getProtocolVersion() {
        return ConnectionInfo.protocolVersion;
    }

    @Override
    public IProtocol byProtocolVersion(int version) {
        ConnectionMode protocol = ConnectionMode.byValue(version);
        return protocol == ConnectionMode.AUTO ? null : protocol;
    }

    @Override
    public List<IProtocol> getSupportedProtocols() {
        return Arrays.asList(ConnectionMode.protocolValues());
    }

    @Override
    public <T> boolean doesServerKnow(Registry<T> registry, T value) {
        return PacketSystem.doesServerKnow(registry, value);
    }

    @Override
    public <T> boolean doesServerKnow(Registry<T> registry, ResourceKey<T> key) {
        return PacketSystem.doesServerKnow(registry, key);
    }

    //region deprecated stuff

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void addClientboundIdentifierCustomPayloadListener(ICustomPayloadListener<ResourceLocation> listener) {
        CustomPayloadHandler.addClientboundIdentifierCustomPayloadListener(listener);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void removeClientboundIdentifierCustomPayloadListener(ICustomPayloadListener<ResourceLocation> listener) {
        CustomPayloadHandler.removeClientboundIdentifierCustomPayloadListener(listener);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void addClientboundStringCustomPayloadListener(ICustomPayloadListener<String> listener) {
        CustomPayloadHandler.addClientboundStringCustomPayloadListener(listener);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void removeClientboundStringCustomPayloadListener(ICustomPayloadListener<String> listener) {
        CustomPayloadHandler.removeClientboundStringCustomPayloadListener(listener);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Contract("null, _, _ -> fail")
    @Override
    public void forceSendCustomPayload(@Nullable ClientPacketListener connection, ResourceLocation channel, FriendlyByteBuf data) {
        if (connection == null) {
            throw new IllegalStateException("Trying to send custom payload when not in-game");
        }
        CustomPayloadHandler.forceSendIdentifierCustomPayload(connection, channel, data);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Contract("null, _, _ -> fail")
    @Override
    public void forceSendStringCustomPayload(@Nullable ClientPacketListener connection, String channel, FriendlyByteBuf data) {
        if (connection == null) {
            throw new IllegalStateException("Trying to send custom payload when not in-game");
        }
        if (ConnectionInfo.protocolVersion > Protocols.V1_12_2) {
            throw new IllegalStateException("Trying to send string custom payload to " + ConnectionMode.byValue(ConnectionInfo.protocolVersion).getName() + " server");
        }
        CustomPayloadHandler.forceSendStringCustomPayload(connection, channel, data);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void addServerboundIdentifierCustomPayloadListener(ICustomPayloadListener<ResourceLocation> listener) {
        CustomPayloadHandler.addServerboundIdentifierCustomPayloadListener(listener);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void removeServerboundIdentifierCustomPayloadListener(ICustomPayloadListener<ResourceLocation> listener) {
        CustomPayloadHandler.removeServerboundIdentifierCustomPayloadListener(listener);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void addServerboundStringCustomPayloadListener(ICustomPayloadListener<String> listener) {
        CustomPayloadHandler.addServerboundStringCustomPayloadListener(listener);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void removeServerboundStringCustomPayloadListener(ICustomPayloadListener<String> listener) {
        CustomPayloadHandler.removeServerboundStringCustomPayloadListener(listener);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void addIdentifierCustomPayloadListener(IIdentifierCustomPayloadListener listener) {
        addClientboundIdentifierCustomPayloadListener(new IdentifierCustomPayloadListenerProxy(listener));
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void removeIdentifierCustomPayloadListener(IIdentifierCustomPayloadListener listener) {
        removeClientboundIdentifierCustomPayloadListener(new IdentifierCustomPayloadListenerProxy(listener));
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void addStringCustomPayloadListener(IStringCustomPayloadListener listener) {
        addClientboundStringCustomPayloadListener(new StringCustomPayloadListenerProxy(listener));
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void removeStringCustomPayloadListener(IStringCustomPayloadListener listener) {
        removeClientboundStringCustomPayloadListener(new StringCustomPayloadListenerProxy(listener));
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void addServerboundIdentifierCustomPayloadListener(IIdentifierCustomPayloadListener listener) {
        addServerboundIdentifierCustomPayloadListener(new IdentifierCustomPayloadListenerProxy(listener));
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void removeServerboundIdentifierCustomPayloadListener(IIdentifierCustomPayloadListener listener) {
        removeServerboundIdentifierCustomPayloadListener(new IdentifierCustomPayloadListenerProxy(listener));
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void addServerboundStringCustomPayloadListener(IStringCustomPayloadListener listener) {
        addServerboundStringCustomPayloadListener(new StringCustomPayloadListenerProxy(listener));
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void removeServerboundStringCustomPayloadListener(IStringCustomPayloadListener listener) {
        removeServerboundStringCustomPayloadListener(new StringCustomPayloadListenerProxy(listener));
    }

    @Deprecated
    private record IdentifierCustomPayloadListenerProxy(
            IIdentifierCustomPayloadListener delegate
    ) implements ICustomPayloadListener<ResourceLocation> {
        @Override
        public void onCustomPayload(ICustomPayloadEvent<ResourceLocation> event) {
            delegate.onCustomPayload(event.getProtocol(), event.getChannel(), event.getData());
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IdentifierCustomPayloadListenerProxy && delegate.equals(((IdentifierCustomPayloadListenerProxy) obj).delegate);
        }
    }

    @Deprecated
    private record StringCustomPayloadListenerProxy(
            IStringCustomPayloadListener delegate
    ) implements ICustomPayloadListener<String> {
        @Override
        public void onCustomPayload(ICustomPayloadEvent<String> event) {
            delegate.onCustomPayload(event.getProtocol(), event.getChannel(), event.getData());
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StringCustomPayloadListenerProxy && delegate.equals(((StringCustomPayloadListenerProxy) obj).delegate);
        }
    }

    //endregion
}
