package sku.dnsresolver.network;

public class DatagramFactory implements PacketTransceiverFactory {

    private int timeout = 0;

    @Override
    public PacketTransceiver createTransceiver() {
        return new DatagramPacketTransceiver(timeout);
    }

    @Override
    public void setTimeout(int milliseconds) {
        timeout = milliseconds;
    }

}
