package sku.dnsresolver.network;

import sku.dnsresolver.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final PacketTransceiverFactory factory;
    private final DNSMessageListener messageListener;

    public NetworkManager(PacketTransceiverFactory factory, DNSMessageListener dnsMessageListener) {
        this.factory = factory;
        this.messageListener = dnsMessageListener;
    }

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
        executor.submit(runnable);
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    private PacketTransceiver createTransceiver() {
        return factory.createTransceiver();
    }

    private void sendQuery(PacketTransceiver transceiver, DNSPacket query, DNSSocketAddress serverAddress) {
        byte[] queryInBytes = new DNSPacketGenerator(query).getBytes();
        PacketTransceiver.Packet queryPacket = new PacketTransceiver.Packet(serverAddress.inetSocketAddress(), queryInBytes);
        transceiver.sendPacket(queryPacket);
    }

    private DNSMessage receiveResponse(PacketTransceiver transceiver) {
        PacketTransceiver.Packet responsePacket = transceiver.receivePacket();
        DNSSocketAddress serverAddress = DNSSocketAddress.from(responsePacket.address);
        DNSPacket response = new DNSPacketParser(responsePacket.data).getDNSPacket();
        return new DNSMessage(serverAddress, response);
    }

    private void notifyListener(DNSMessage message) {
        messageListener.receivedMessage(message);
    }
}
