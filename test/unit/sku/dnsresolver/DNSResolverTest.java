package sku.dnsresolver;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import sku.dnsresolver.network.DNSSocketAddress;
import sku.dnsresolver.network.NetworkExecutor;
import sku.dnsresolver.ui.UiListener;

import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

@RunWith(JMock.class)
public class DNSResolverTest {
    private final Mockery context = new Mockery();

    private final UiListener uiListener = context.mock(UiListener.class);

    private final FakeExecutor fakeExecutor = new FakeExecutor();

    private final DNSResolver resolver = new DNSResolver(fakeExecutor, uiListener);

    @Test
    public void resolvesWithRecursion() {
        final DNSSocketAddress serverSocket = new DNSSocketAddress("127.0.0.1", "53");

        final String resolvedAddress = "192.168.0.1";
        final String query = "www.example.com";

        fakeExecutor.addResponseFor(serverSocket.ipAddress, query, (short) 1, resolvedAddress);

        context.checking(new Expectations() {{
            oneOf(uiListener).responseText(with(allOf(
                    containsString("label: " + query),
                    containsString("address: " + resolvedAddress)
            )));
        }});

        resolver.resolve(query, serverSocket.ipAddress, serverSocket.port, true);
    }

    @Test
    public void resolvesWithoutRecursion() {
        String ROOT_NS_SERVER = "a.root-servers.net";
        String ROOT_NS_SERVER_IP = "127.0.0.2";

        String COM_NS_SERVER = "a.gtld-servers.net";
        String COM_NS_SERVER_IP = "127.0.0.3";

        String EXAMPLE_NS_SERVER = "ns.example.com";
        String EXAMPLE_NS_SERVER_IP = "127.0.0.4";

        String WWW_EXAMPLE_COM_IP = "192.168.0.1";

        fakeExecutor.addResponseFor("127.0.0.1", "", (short) 2, ROOT_NS_SERVER);
        fakeExecutor.addResponseFor("127.0.0.1", ROOT_NS_SERVER, (short) 1, ROOT_NS_SERVER_IP);

        fakeExecutor.addResponseFor(ROOT_NS_SERVER_IP, "com", (short) 2, COM_NS_SERVER);
        fakeExecutor.addResponseFor(ROOT_NS_SERVER_IP, COM_NS_SERVER, (short) 1, COM_NS_SERVER_IP);

        fakeExecutor.addResponseFor(COM_NS_SERVER_IP, "example.com", (short) 2, EXAMPLE_NS_SERVER);
        fakeExecutor.addResponseFor(COM_NS_SERVER_IP, EXAMPLE_NS_SERVER, (short) 1, EXAMPLE_NS_SERVER_IP);

        fakeExecutor.addResponseFor(EXAMPLE_NS_SERVER_IP, "www.example.com", (short) 2, EXAMPLE_NS_SERVER);
        fakeExecutor.addResponseFor(EXAMPLE_NS_SERVER_IP, EXAMPLE_NS_SERVER, (short) 1, EXAMPLE_NS_SERVER_IP);
        fakeExecutor.addResponseFor(EXAMPLE_NS_SERVER_IP, "www.example.com", (short) 1, WWW_EXAMPLE_COM_IP);

        context.checking(new Expectations() {{
            oneOf(uiListener).responseText(with(allOf(
                    containsString("label: " + "www.example.com"),
                    containsString("address: " + WWW_EXAMPLE_COM_IP)
            )));
        }});

        resolver.resolve("www.example.com", "127.0.0.1", "53", false);
    }


    private class FakeExecutor implements NetworkExecutor {

        private final HashMap<DNSSocketAddress, HashMap<DNSPacket.DNSQuery, DNSPacket.DNSAnswer>> responses = new HashMap<>();


        @Override
        public void query(DNSSocketAddress serverAddress, DNSPacket packet) {
            final HashMap<DNSPacket.DNSQuery, DNSPacket.DNSAnswer> queryHashmap = responses.get(serverAddress);
            DNSPacket.DNSAnswer answer = queryHashmap.get(packet.queries[0]);

            DNSPacket responsePacket = buildResponsePacket(packet.id, packet.recursionDesired, answer);
            resolver.receiveMessage(new DNSMessage(serverAddress, responsePacket));
        }

        @Override
        public void shutdown() {

        }

        public void addResponseFor(String serverIpAddress, String queryString, short type, String response) {
            DNSPacket.DNSQuery query = new DNSPacket.DNSQuery(queryString, type, (short) 1);
            DNSPacket.DNSAnswer answer = new DNSPacket.DNSAnswer(query, 0, (short) 0, response);

            DNSSocketAddress serverSocket = new DNSSocketAddress(serverIpAddress, "53");
            if (!responses.containsKey(serverSocket)) {
                responses.put(serverSocket, new HashMap<>());
            }

            final HashMap<DNSPacket.DNSQuery, DNSPacket.DNSAnswer> queryHashmap = responses.get(serverSocket);
            queryHashmap.put(query, answer);
        }

        private DNSPacket buildResponsePacket(short id, boolean recursion, DNSPacket.DNSAnswer answer) {
            return new DNSPacketBuilder()
                    .setId(id)
                    .setResponse(true)
                    .setOpCode(0)
                    .setAuthoritative(false)
                    .setTruncated(false)
                    .setRecursionDesired(recursion)
                    .setRecursionAvailable(recursion)
                    .setZ(false)
                    .setAnswerAuthenticated(false)
                    .setNonAuthenticatedData(false)
                    .setReplyCode(0)
                    .setQuestionCount((short) 1)
                    .setAnswerRRCount((short) 1)
                    .setAuthorityRRCount((short) 0)
                    .setAdditionalRRCount((short) 0)
                    .setQueries(answer.query)
                    .setAnswers(answer)
                    .build();
        }
    }

}