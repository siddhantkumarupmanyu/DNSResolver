package sku.dnsresolver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DNSThread extends Thread {

    private final DatagramSocket socket;

    private final ExecutorService sender = Executors.newSingleThreadExecutor();
    private final DNSMessageListener messageListener;

    private boolean active = true;

    public DNSThread(DNSMessageListener dnsMessageListener) throws SocketException {
        this.socket = new DatagramSocket();
        this.messageListener = dnsMessageListener;
    }

    @Override
    public void run() {
        while (active) {
            try {
                DatagramPacket packet = receiveDatagramPacket();
                DNSMessage message = createDNSMessageFromReceivedPacket(packet);
                notifyMessageListener(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
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
        sender.shutdown();
    }

    private DatagramPacket receiveDatagramPacket() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return packet;
    }

    private DNSMessage createDNSMessageFromReceivedPacket(DatagramPacket packet) {
        DNSProtocol protocol = createDNSProtocolFromPacket(packet);
        DNSSocketAddress socketAddress = DNSSocketAddress.from((InetSocketAddress) packet.getSocketAddress());
        return new DNSMessage(socketAddress, protocol);
    }

    private DNSProtocol createDNSProtocolFromPacket(DatagramPacket packet) {
        String response = new String(packet.getData(), 0, packet.getLength());
        return new DNSProtocol(response);
    }

    private void notifyMessageListener(DNSMessage message) {
        messageListener.message(message);
    }

    private void sendPacket(String domainName, InetSocketAddress socketAddress) throws IOException {
        byte[] request = domainName.getBytes();
        DatagramPacket packet = new DatagramPacket(request, request.length, socketAddress);
        socket.send(packet);
    }
}
