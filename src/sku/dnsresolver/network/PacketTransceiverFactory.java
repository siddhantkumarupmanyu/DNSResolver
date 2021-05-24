package sku.dnsresolver.network;

public interface PacketTransceiverFactory {
    PacketTransceiver createTransceiver();
    void setTimeout(int milliseconds);
}
