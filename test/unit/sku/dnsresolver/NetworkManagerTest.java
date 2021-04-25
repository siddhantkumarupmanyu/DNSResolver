package sku.dnsresolver;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

// TODO:
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
    @Ignore
    public void queryDnsPacket() throws Exception {
        final CountDownLatch messageWasReceived = new CountDownLatch(1);
        NetworkManager networkManager = new NetworkManager(transceiver, createDNSMessageListener(messageWasReceived));

        networkManager.query(fakeServerAddress, queryFor("www.example.com"));

        fakeDnsServer.hasReceivedRequestFor("www.example.com");
        fakeDnsServer.respondWith("127.0.0.1");

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
}

// this is an integration test