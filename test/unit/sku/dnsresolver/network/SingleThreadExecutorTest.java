package sku.dnsresolver.network;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sku.dnsresolver.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

public class SingleThreadExecutorTest {

    private final FakeDnsServer fakeDnsServer = new FakeDnsServer();
    private final DNSSocketAddress fakeServerAddress = new DNSSocketAddress(fakeDnsServer.ipAddress(), fakeDnsServer.port());

    private final PacketTransceiverFactory factory = new DatagramFactory();

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
        SingleThreadExecutor singleThreadExecutor = new SingleThreadExecutor(factory, createDNSMessageListener(messageWasReceived));

        singleThreadExecutor.query(fakeServerAddress, queryFor("www.example.com"));

        fakeDnsServer.hasReceivedPacket(SamplePackets.QUERY_WWW_EXAMPLE_COM);
        fakeDnsServer.respondWith(SamplePackets.RESPONSE_WWW_EXAMPLE_COM);

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
                .setId(SamplePackets.DEFAULT_ID)
                .setRecursionDesired(SamplePackets.DEFAULT_RECURSION)
                .setQueries(new DNSPacket.DNSQuery(query, SamplePackets.DEFAULT_QTYPE, SamplePackets.DEFAULT_QCLASS))
                .build();
    }

}

// this is an integration test