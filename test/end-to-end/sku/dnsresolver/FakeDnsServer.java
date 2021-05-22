package sku.dnsresolver;

import sku.dnsresolver.network.DNSSocketAddress;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class FakeDnsServer {
    public static final String FAKE_DNS_IP_ADDRESS = "127.0.0.1";
    public static final String FAKE_DNS_PORT = "5000";

    private NetworkThread serverThread;
    private final SinglePacketReceiver messageListener = new SinglePacketReceiver();
    private DNSPacket queryDnsPacket;

    public void startServer() throws Exception {
        DNSSocketAddress socketAddress = new DNSSocketAddress(FAKE_DNS_IP_ADDRESS, FAKE_DNS_PORT);
        serverThread = new NetworkThread(messageListener, socketAddress);
        serverThread.start();
    }

    public void stopServer() {
        serverThread.stopThread();
    }

    public void hasReceivedPacket(byte[] packet) throws InterruptedException {
        messageListener.receivedAPacket(packet);
    }

    public void respondWith(byte[] packet) {
        packet[0] = messageListener.lastId[0];
        packet[1] = messageListener.lastId[1];
        serverThread.sendPacket(packet, messageListener.lastAddress);
    }

    public String ipAddress() {
        return FAKE_DNS_IP_ADDRESS;
    }

    public String port() {
        return FAKE_DNS_PORT;
    }

    public static class SinglePacketReceiver {
        private final ArrayBlockingQueue<byte[]> packets = new ArrayBlockingQueue<>(1);

        private DNSSocketAddress lastAddress;
        private byte[] lastId;

        public void receivedPacket(byte[] packet, DNSSocketAddress address) {
            packets.add(packet);
            lastId = Arrays.copyOf(packet, 2);
            lastAddress = address;
        }

        public void receivedAPacket(byte[] packet) throws InterruptedException {
            byte[] expectedPacketWithoutId = Arrays.copyOfRange(packet, 2, packet.length - 2);
            byte[] originalPacketWithoutId = Arrays.copyOfRange(Objects.requireNonNull(packets.poll(5, TimeUnit.SECONDS)), 2, packet.length - 2);
            assertThat("Packet in Bytes", originalPacketWithoutId, is(equalTo(expectedPacketWithoutId)));
        }
    }
}
