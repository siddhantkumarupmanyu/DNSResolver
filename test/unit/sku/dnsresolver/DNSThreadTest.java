package sku.dnsresolver;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;

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
        CountDownLatch messageWasReceived = new CountDownLatch(1);
        DNSThread dnsThread = new DNSThread(createDNSMessageListener(
                messageWasReceived,
                withSocketAddress(any(DNSSocketAddress.class))
        ));

        dnsThread.start();
        dnsThread.sendRequest("example.com", new DNSSocketAddress(fakeDnsServer.ipAddress(), fakeDnsServer.port()));
        fakeDnsServer.hasReceivedRequestFor("example.com");
        fakeDnsServer.respondWith("127.0.0.1");

        assertThat("should have received response", messageWasReceived.await(4, TimeUnit.SECONDS));
    }

    private DNSMessageListener createDNSMessageListener(final CountDownLatch countDownLatch, Matcher<DNSMessage> dnsMessageMatcher) {
        return new DNSMessageListener() {

            @Override
            public void message(DNSMessage dnsMessage) {
                countDownLatch.countDown();
                assertThat("DNS message", dnsMessage, dnsMessageMatcher);
            }
        };
    }

    private Matcher<DNSMessage> withSocketAddress(Matcher<DNSSocketAddress> matcher) {
        return new FeatureMatcher<DNSMessage, DNSSocketAddress>(matcher, "DNSMessage with socket address ", "was") {
            @Override
            protected DNSSocketAddress featureValueOf(DNSMessage actual) {
                return actual.from;
            }
        };
    }
}

// this is a integration test