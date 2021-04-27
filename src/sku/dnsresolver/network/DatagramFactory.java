package sku.dnsresolver.network;

public class DatagramFactory implements PacketTransceiverFactory {

    @Override
    public PacketTransceiver createTransceiver() {
        return new DatagramPacketTransceiver();
    }
    
}
