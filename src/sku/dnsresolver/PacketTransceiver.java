package sku.dnsresolver;

public interface PacketTransceiver {
    byte readNextByte();

    byte[] readHeaderBytes();
}
