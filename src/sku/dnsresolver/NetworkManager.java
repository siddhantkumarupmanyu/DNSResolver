package sku.dnsresolver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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

    public void query(final DNSSocketAddress serverAddress, final DNSPacket dnsPacket) {
        Runnable runnable = new QueryRunnable(serverAddress, dnsPacket);
        executor.submit(runnable);
    }

    private static class QueryRunnable implements Runnable {

        private final DNSSocketAddress serverAddress;
        private final DNSPacket dnsPacket;

        public QueryRunnable(DNSSocketAddress serverAddress, DNSPacket dnsPacket) {
            this.serverAddress = serverAddress;
            this.dnsPacket = dnsPacket;
        }

        @Override
        public void run() {
            try {
                DatagramSocket socket = createSocket();

                byte[] request = new DNSPacketGenerator(dnsPacket).getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(request, request.length, serverAddress.inetSocketAddress());
                socket.send(datagramPacket);

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private DatagramSocket createSocket() throws SocketException {
            return new DatagramSocket();
        }
    }

}
