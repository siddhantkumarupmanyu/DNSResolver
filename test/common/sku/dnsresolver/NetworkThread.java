package sku.dnsresolver;

import sku.dnsresolver.network.DNSSocketAddress;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static sku.dnsresolver.network.DatagramPacketTransceiver.UDP_MAX_BYTES;

public class NetworkThread extends Thread {

    private final DatagramSocket socket;

    private final ExecutorService sender = Executors.newSingleThreadExecutor();
    private final FakeDnsServer.SinglePacketReceiver receiver;

    private boolean active = true;

    public NetworkThread(FakeDnsServer.SinglePacketReceiver receiver, DNSSocketAddress socketAddress) throws Exception {
        this.receiver = receiver;
        this.socket = new DatagramSocket(socketAddress.inetSocketAddress());
    }

    @Override
    public void run() {
        while (active) {
            try {
                DatagramPacket datagramPacket = receiveDatagramPacket();
                byte[] packetInBytes = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());

                notifyMessageListener(packetInBytes,
                        DNSSocketAddress.from((InetSocketAddress) datagramPacket.getSocketAddress()));
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }

    private void notifyMessageListener(byte[] packet, DNSSocketAddress address) {
        receiver.receivedPacket(packet, address);
    }

    public void sendPacket(final byte[] packet, final DNSSocketAddress dnsSocketAddress) {
        sender.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendBytes(packet, dnsSocketAddress.inetSocketAddress());
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
        byte[] receiveBuffer = new byte[UDP_MAX_BYTES];
        DatagramPacket packet = new DatagramPacket(receiveBuffer, UDP_MAX_BYTES);
        socket.receive(packet);
        return packet;
    }

    private void sendBytes(byte[] packet, InetSocketAddress socketAddress) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(packet, packet.length, socketAddress);
        socket.send(datagramPacket);
    }
}
