package sku.dnsresolver;

public class DatagramPacketTransceiver implements PacketTransceiver {
    @Override
    public byte[] readNextBytes(int number) {
        return new byte[0];
    }
}
