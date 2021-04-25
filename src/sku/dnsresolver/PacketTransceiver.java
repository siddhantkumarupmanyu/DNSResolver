package sku.dnsresolver;

import java.net.InetSocketAddress;

public interface PacketTransceiver {
    void sendPacket(byte[] packet, InetSocketAddress socketAddress);

    byte[] receivePacket();
}
