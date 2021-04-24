package sku.dnsresolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
//
//public class NetworkThreadTest {
//
//    private static final DNSMessageListener NO_MESSAGE_LISTENER = null;
//
//    private final FakeDnsServer fakeDnsServer = new FakeDnsServer();
//
//    @Before
//    public void setUp() throws Exception {
//        fakeDnsServer.startServer();
//    }
//
//    @After
//    public void tearDown() {
//        fakeDnsServer.stopServer();
//    }
//
//    @Test
//    public void canSendAndReceiveADatagram() throws Exception {
//        final CountDownLatch messageWasReceived = new CountDownLatch(1);
//        NetworkThread networkThread = new NetworkThread(createDNSMessageListener(messageWasReceived));
//
//        networkThread.start();
//        networkThread.sendRequest("example.com", new DNSSocketAddress(fakeDnsServer.ipAddress(), fakeDnsServer.port()));
//        fakeDnsServer.hasReceivedRequestFor("example.com");
//        fakeDnsServer.respondWith("127.0.0.1");
//
//        assertThat("should have received response", messageWasReceived.await(4, TimeUnit.SECONDS));
//
//        networkThread.stopThread();
//    }
//
//    @Test
//    public void bindToSameDNSSocketAddressAfterThreadIsStopped() throws Exception {
//        DNSSocketAddress socketAddress = new DNSSocketAddress("127.0.0.1", "6000");
//        NetworkThread thread = new NetworkThread(NO_MESSAGE_LISTENER, socketAddress);
//        thread.start();
//        thread.stopThread();
//
//        thread = new NetworkThread(NO_MESSAGE_LISTENER, socketAddress);
//        thread.start();
//        thread.stopThread();
//    }
//
//    private DNSMessageListener createDNSMessageListener(final CountDownLatch countDownLatch) {
//        return new DNSMessageListener() {
//
//            @Override
//            public void message(DNSMessage dnsMessage) {
//                countDownLatch.countDown();
//            }
//        };
//    }
//}

// this is a integration test