package sku.dnsresolver.network;

import java.net.InetSocketAddress;

public interface PacketTransceiver {

    void sendPacket(PacketTransceiver.Packet packet);

    PacketTransceiver.Packet receivePacket();

    class Packet {
        public final InetSocketAddress address;
        public final byte[] data;

        public Packet(InetSocketAddress address, byte[] data) {
            this.address = address;
            this.data = data;
        }
    }
}
