package sku.dnsresolver.network;

import sku.dnsresolver.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadExecutor implements NetworkExecutor {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final PacketTransceiverFactory factory;
    private final DNSMessageListener messageListener;

    public SingleThreadExecutor(PacketTransceiverFactory factory, DNSMessageListener dnsMessageListener) {
        this.factory = factory;
        this.messageListener = dnsMessageListener;
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
        executor.submit(runnable);
    }

    @Override
    public void shutdown() {
        executor.shutdownNow();
    }

    private PacketTransceiver createTransceiver() {
        return factory.createTransceiver();
    }

    private void sendQuery(PacketTransceiver transceiver, DNSPacket query, DNSSocketAddress serverAddress) {
        // TODO: replace it with something like DNSPacketGenerator.packetInBytes(query);
        byte[] queryInBytes = new DNSPacketGenerator(query).getBytes();
        PacketTransceiver.Packet queryPacket = new PacketTransceiver.Packet(serverAddress.inetSocketAddress(), queryInBytes);
        transceiver.sendPacket(queryPacket);
    }

    private DNSMessage receiveResponse(PacketTransceiver transceiver) {
        PacketTransceiver.Packet responsePacket = transceiver.receivePacket();
        DNSSocketAddress serverAddress = DNSSocketAddress.from(responsePacket.address);
        // TODO: replace it with something like DNSPaketParser.parsePacket(response.data);
        DNSPacket response = new DNSPacketParser(responsePacket.data).getDNSPacket();
        return new DNSMessage(serverAddress, response);
    }

    private void notifyListener(DNSMessage message) {
        messageListener.receivedMessage(message);
    }
}
