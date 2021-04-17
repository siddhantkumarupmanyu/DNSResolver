import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FakeDnsServer {
    public static final String FAKE_DNS_IP_ADDRESS = "127.0.0.1";
    public static final String FAKE_DNS_PORT = "5000";

    private FakeServerThread serverThread;
    private final SingleMessageListener messageListener = new SingleMessageListener();

    public void startServer() throws IOException {
        serverThread = new FakeServerThread(messageListener);
        serverThread.start();
    }

    public void stopServer() {
        serverThread.stopThread();
    }

    public void hasReceivedRequestFor(String domainName) throws InterruptedException {
        messageListener.receivesAMessage();
    }

    public void respondWith(String s) {

    }

    public String ipAddress() {
        return FAKE_DNS_IP_ADDRESS;
    }


    public String port() {
        return FAKE_DNS_PORT;
    }

    public static class SingleMessageListener implements RequestListener {
        private final ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(1);

        @Override
        public void newRequest(String request) {
            messages.add(request);
        }

        public void receivesAMessage() throws InterruptedException {
//            assertThat("DNSMessage", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
            assertThat("DNSMessage", messages.poll(5, TimeUnit.SECONDS), is("A random value"));
        }
    }
}
