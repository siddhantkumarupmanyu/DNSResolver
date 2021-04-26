package sku.dnsresolver;

import sku.dnsresolver.network.DNSSocketAddress;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FakeDnsServer {
    public static final String FAKE_DNS_IP_ADDRESS = "127.0.0.1";
    public static final String FAKE_DNS_PORT = "5000";

    public static final short DEFAULT_ID = 1;
    public static final boolean DEFAULT_RECURSION = true;
    public static final short DEFAULT_QTYPE = 1;
    public static final short DEFAULT_QCLASS = 1;

    private NetworkThread serverThread;
    private final SingleMessageListener messageListener = new SingleMessageListener();
    private DNSPacket queryDnsPacket;

    public void startServer() throws Exception {
        DNSSocketAddress socketAddress = new DNSSocketAddress(FAKE_DNS_IP_ADDRESS, FAKE_DNS_PORT);
        serverThread = new NetworkThread(messageListener, socketAddress);
        serverThread.start();
    }

    public void stopServer() {
        serverThread.stopThread();
    }

    public void hasReceivedPacket(String query) throws InterruptedException {
        DNSPacket packet = queryFor(query);
        messageListener.receivesAMessageWith(packet);
        this.queryDnsPacket = packet;
    }

    public void respondWith(byte[] ipAddress) {
        serverThread.sendDNSPacketWithAnswer(queryDnsPacket, ipAddress ,messageListener.lastAddress);
    }

    private DNSPacket queryFor(String query) {
        return new DNSQueryBuilder()
                .setId(DEFAULT_ID)
                .setRecursionDesired(DEFAULT_RECURSION)
                .setQueries(new DNSPacket.DNSQuery(query, DEFAULT_QTYPE, DEFAULT_QCLASS))
                .build();
    }

    public String ipAddress() {
        return FAKE_DNS_IP_ADDRESS;
    }

    public String port() {
        return FAKE_DNS_PORT;
    }

    public static class SingleMessageListener implements DNSMessageListener {
        private final ArrayBlockingQueue<DNSPacket> packets = new ArrayBlockingQueue<>(1);

        private DNSSocketAddress lastAddress;

        @Override
        public void receivedMessage(DNSMessage message) {
            packets.add(message.packet);
            lastAddress = message.from;
        }

        public void receivesAMessageWith(DNSPacket dnsPacket) throws InterruptedException {
            assertThat("DNS Packet", packets.poll(5, TimeUnit.SECONDS), is(dnsPacket));
        }
    }
}
