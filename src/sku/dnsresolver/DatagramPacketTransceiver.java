package sku.dnsresolver;

public class DatagramPacketTransceiver implements PacketTransceiver {
    @Override
    public byte readNextByte() {
        return 0;
    }

    @Override
    public byte[] readHeaderBytes() {
        return new byte[0];
    }
    // TODO
}
