package sku.dnsresolver;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FakeDnsServer {
    public static final String FAKE_DNS_IP_ADDRESS = "127.0.0.1";
    public static final String FAKE_DNS_PORT = "5000";

    private NetworkThread serverThread;
    private final SingleMessageListener messageListener = new SingleMessageListener();

    public void startServer() throws Exception {
        DNSSocketAddress socketAddress = new DNSSocketAddress(FAKE_DNS_IP_ADDRESS, FAKE_DNS_PORT);
        serverThread = new NetworkThread(messageListener, socketAddress);
        serverThread.start();
    }

    public void stopServer() {
        serverThread.stopThread();
    }

    public void hasReceivedRequestFor(String domainName) throws InterruptedException {
        messageListener.receivesAMessage(domainName);
    }

    public void respondWith(String s) {
        serverThread.sendRequest(s, messageListener.lastAddress);
    }

    public String ipAddress() {
        return FAKE_DNS_IP_ADDRESS;
    }


    public String port() {
        return FAKE_DNS_PORT;
    }

    public static class SingleMessageListener implements DNSMessageListener {
        private final ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(1);

        private DNSSocketAddress lastAddress;

        @Override
        public void message(DNSMessage message) {
            messages.add(message.protocol.message);
            lastAddress = message.from;
        }

        public void receivesAMessage(String domainName) throws InterruptedException {
            assertThat("DNS Message", messages.poll(5, TimeUnit.SECONDS), is(domainName));
        }
    }
}
