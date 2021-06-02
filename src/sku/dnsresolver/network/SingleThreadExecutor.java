package sku.dnsresolver.network;

import sku.dnsresolver.*;
import sku.dnsresolver.util.Announcer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadExecutor implements NetworkExecutor {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final PacketTransceiverFactory factory;
    private final Announcer<DNSMessageListener> listeners = Announcer.to(DNSMessageListener.class);

    public SingleThreadExecutor(PacketTransceiverFactory factory) {
        this.factory = factory;
    }

    @Override
    public void addListener(DNSMessageListener listener) {
        listeners.addListener(listener);
    }

    @Override
    public void query(final DNSSocketAddress serverAddress, final DNSPacket query) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PacketTransceiver transceiver = createTransceiver();
                sendQuery(transceiver, query, serverAddress);
                DNSMessage message = receiveResponse(transceiver);
                notifyListener(message);
            }
        };
        executor.execute(runnable);
    }

    @Override
    public void shutdown() {
        executor.shutdownNow();
    }

    private PacketTransceiver createTransceiver() {
        return factory.createTransceiver();
    }

    private void sendQuery(PacketTransceiver transceiver, DNSPacket query, DNSSocketAddress serverAddress) {
        byte[] queryInBytes = DNSPacketGenerator.packetInBytes(query);
        PacketTransceiver.Packet queryPacket = new PacketTransceiver.Packet(serverAddress.inetSocketAddress(), queryInBytes);
        transceiver.sendPacket(queryPacket);
    }

    private DNSMessage receiveResponse(PacketTransceiver transceiver) {
        PacketTransceiver.Packet responsePacket = transceiver.receivePacket();
        DNSSocketAddress serverAddress = DNSSocketAddress.from(responsePacket.address);
        DNSPacket response = DNSPacketParser.parsePacket(responsePacket.data);
        return new DNSMessage(serverAddress, response);
    }

    private void notifyListener(DNSMessage message) {
        listeners.announce().receiveMessage(message);
    }
}
