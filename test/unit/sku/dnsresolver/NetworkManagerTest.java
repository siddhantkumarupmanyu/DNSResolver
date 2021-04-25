package sku.dnsresolver;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

public class NetworkManagerTest {

    private final FakeDnsServer fakeDnsServer = new FakeDnsServer();
    private final DNSSocketAddress fakeServerAddress = new DNSSocketAddress(fakeDnsServer.ipAddress(), fakeDnsServer.port());

    private final PacketTransceiver transceiver = new DatagramPacketTransceiver();

    @Before
    public void setUp() throws Exception {
        fakeDnsServer.startServer();
    }

    @After
    public void tearDown() {
        fakeDnsServer.stopServer();
    }

    @Test
    public void queryDnsPacket() throws Exception {
        final CountDownLatch messageWasReceived = new CountDownLatch(1);
        NetworkManager networkManager = new NetworkManager(transceiver, createDNSMessageListener(messageWasReceived));

        networkManager.query(fakeServerAddress, queryFor("www.example.com"));

        fakeDnsServer.hasReceivedPacket("www.example.com");
        fakeDnsServer.respondWith(address_172_217_160_196_inBytes());

        assertThat("should have received response", messageWasReceived.await(4, TimeUnit.SECONDS));
    }

    private DNSMessageListener createDNSMessageListener(final CountDownLatch countDownLatch) {
        return new DNSMessageListener() {

            @Override
            public void receivedMessage(DNSMessage dnsMessage) {
                countDownLatch.countDown();
            }
        };
    }

    private DNSPacket queryFor(String query) {
        return new DNSQueryBuilder()
                .setId(FakeDnsServer.DEFAULT_ID)
                .setRecursionDesired(FakeDnsServer.DEFAULT_RECURSION)
                .setQueries(new DNSPacket.DNSQuery(query, FakeDnsServer.DEFAULT_QTYPE, FakeDnsServer.DEFAULT_QCLASS))
                .build();
    }

    private byte[] address_172_217_160_196_inBytes() {
        return new byte[]{(byte) 0xac, (byte) 0xd9, (byte) 0xa0, (byte) 0xc4};
    }
}

// this is an integration test