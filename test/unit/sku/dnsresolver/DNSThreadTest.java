package sku.dnsresolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

public class DNSThreadTest {

    private final FakeDnsServer fakeDnsServer = new FakeDnsServer();

    @Before
    public void setUp() throws IOException {
        fakeDnsServer.startServer();
    }

    @After
    public void tearDown() {
        fakeDnsServer.stopServer();
    }

    @Test
    public void canSendAndReceiveADatagram() throws Exception {
        CountDownLatch datagramWasReceived = new CountDownLatch(1);
        DNSThread dnsThread = new DNSThread(createDnsMessageListener(datagramWasReceived));

        dnsThread.start();
        dnsThread.sendRequest("example.com", new DNSSocketAddress(fakeDnsServer.ipAddress(), fakeDnsServer.port()));
        fakeDnsServer.hasReceivedRequestFor("example.com");
        fakeDnsServer.respondWith("127.0.0.1");

        assertThat("should have received response", datagramWasReceived.await(4, TimeUnit.SECONDS));
    }

    private DNSMessageListener createDnsMessageListener(final CountDownLatch countDownLatch) {
        return new DNSMessageListener() {

            @Override
            public void message(String dnsMessage) {
                countDownLatch.countDown();
            }
        };
    }
}

// this is a integration test