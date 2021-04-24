package sku.dnsresolver;

public interface PacketTransceiver {
    byte[] readNextBytes(int number);
}
