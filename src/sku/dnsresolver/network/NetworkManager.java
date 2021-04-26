package sku.dnsresolver.network;

import sku.dnsresolver.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkManager {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final PacketTransceiver transceiver;
    private final DNSMessageListener messageListener;

    public NetworkManager(PacketTransceiver transceiver, DNSMessageListener dnsMessageListener) {
        this.transceiver = transceiver;
        this.messageListener = dnsMessageListener;
    }

    public void query(final DNSSocketAddress serverAddress, final DNSPacket query) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                byte[] queryPacket = new DNSPacketGenerator(query).getBytes();
                transceiver.sendPacket(queryPacket, serverAddress.inetSocketAddress());

                byte[] responsePacket = transceiver.receivePacket();
                DNSPacket response = new DNSPacketParser(responsePacket).getDNSPacket();

                messageListener.receivedMessage(new DNSMessage(null, response));
            }
        };
        executor.submit(runnable);
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
