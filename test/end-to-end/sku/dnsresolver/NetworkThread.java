package sku.dnsresolver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkThread extends Thread {

    private final DatagramSocket socket;

    private final ExecutorService sender = Executors.newSingleThreadExecutor();
    private final DNSMessageListener messageListener;

    private boolean active = true;

    public NetworkThread(DNSMessageListener messageListener) throws SocketException {
        this.socket = new DatagramSocket();
        this.messageListener = messageListener;
    }

    public NetworkThread(DNSMessageListener messageListener, DNSSocketAddress socketAddress) throws Exception {
        this.messageListener = messageListener;
        this.socket = new DatagramSocket(socketAddress.inetSocketAddress());
    }

    @Override
    public void run() {
        while (active) {
            try {
                DatagramPacket packet = receiveDatagramPacket();
                DNSMessage message = createDNSMessageFromReceivedPacket(packet);
                notifyMessageListener(message);
            } catch (SocketException e) {
                // not implemented
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendRequest(String domainName, final DNSSocketAddress dnsSocketAddress) {
        sender.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendPacket(domainName, dnsSocketAddress.inetSocketAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopThread() {
        active = false;
        socket.close();
        sender.shutdown();
    }

    private DatagramPacket receiveDatagramPacket() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return packet;
    }

    private DNSMessage createDNSMessageFromReceivedPacket(DatagramPacket datagramPacket) {
        DNSPacket dnsPacket = createDNSPacketFrom(datagramPacket);
        DNSSocketAddress socketAddress = DNSSocketAddress.from((InetSocketAddress) datagramPacket.getSocketAddress());
        return new DNSMessage(socketAddress, dnsPacket);
    }

    private DNSPacket createDNSPacketFrom(DatagramPacket datagramPacket) {
        String query = "test";
        return new DNSQueryBuilder()
                .setId(FakeDnsServer.DEFAULT_ID)
                .setRecursionDesired(FakeDnsServer.DEFAULT_RECURSION)
                .setQueries(new DNSPacket.DNSQuery(query, FakeDnsServer.DEFAULT_QTYPE, FakeDnsServer.DEFAULT_QCLASS))
                .build();
    }

    private void notifyMessageListener(DNSMessage message) {
        messageListener.receivedMessage(message);
    }

    private void sendPacket(String domainName, InetSocketAddress socketAddress) throws IOException {
        byte[] request = domainName.getBytes();
        DatagramPacket packet = new DatagramPacket(request, request.length, socketAddress);
        socket.send(packet);
    }
}
