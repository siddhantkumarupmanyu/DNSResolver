package sku.dnsresolver;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import sku.dnsresolver.network.DNSSocketAddress;
import sku.dnsresolver.network.NetworkExecutor;
import sku.dnsresolver.ui.UiListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        fakeExecutor.addResponseFor("127.0.0.1", "", DNSPacket.TYPE_NS, ROOT_NS_SERVER);
        fakeExecutor.addResponseFor("127.0.0.1", ROOT_NS_SERVER, DNSPacket.TYPE_A, ROOT_NS_SERVER_IP);

        fakeExecutor.addResponseFor(ROOT_NS_SERVER_IP, "com", COM_NS_SERVER, COM_NS_SERVER_IP);

        fakeExecutor.addResponseFor(COM_NS_SERVER_IP, "example.com", DNSPacket.TYPE_NS, EXAMPLE_NS_SERVER);
        fakeExecutor.addResponseFor(COM_NS_SERVER_IP, EXAMPLE_NS_SERVER, DNSPacket.TYPE_A, EXAMPLE_NS_SERVER_IP);

        fakeExecutor.addResponseFor(EXAMPLE_NS_SERVER_IP, "www.example.com", DNSPacket.TYPE_NS, EXAMPLE_NS_SERVER);
        fakeExecutor.addResponseFor(EXAMPLE_NS_SERVER_IP, EXAMPLE_NS_SERVER, DNSPacket.TYPE_A, EXAMPLE_NS_SERVER_IP);
        fakeExecutor.addResponseFor(EXAMPLE_NS_SERVER_IP, "www.example.com", DNSPacket.TYPE_A, WWW_EXAMPLE_COM_IP);

        context.checking(new Expectations() {{
            oneOf(uiListener).responseText(with(allOf(
                    containsString("label: " + "www.example.com"),
                    containsString("address: " + WWW_EXAMPLE_COM_IP)
            )));
        }});

        resolver.resolve("www.example.com", "127.0.0.1", "53", false);
    }


    private class FakeExecutor implements NetworkExecutor {

        private final HashMap<DNSSocketAddress, HashMap<DNSPacket.DNSQuery, DNSPacket.DNSAnswer[]>> responses = new HashMap<>();


        @Override
        public void query(DNSSocketAddress serverAddress, DNSPacket packet) {
            final DNSPacket.DNSQuery askedQuery = packet.queries[0];

            final HashMap<DNSPacket.DNSQuery, DNSPacket.DNSAnswer[]> queryHashmap = responses.get(serverAddress);

            final ArrayList<DNSPacket.DNSAnswer> answersSection = new ArrayList<>();
            final ArrayList<DNSPacket.DNSAnswer> authoritativeNameServes = new ArrayList<>();
            final ArrayList<DNSPacket.DNSAnswer> additionalSection = new ArrayList<>();

            if (queryHashmap.get(askedQuery).length == 1) {
                DNSPacket.DNSAnswer answer = queryHashmap.get(askedQuery)[0];
                answersSection.add(answer);
            } else {
                DNSPacket.DNSAnswer nameServer = queryHashmap.get(askedQuery)[0];
                DNSPacket.DNSAnswer additional = queryHashmap.get(askedQuery)[1];
                authoritativeNameServes.add(nameServer);
                additionalSection.add(additional);
            }

            DNSPacket responsePacket = buildResponsePacket(
                    packet.id, packet.recursionDesired, askedQuery,
                    toDNSAnswerArray(answersSection), toDNSAnswerArray(authoritativeNameServes), toDNSAnswerArray(additionalSection));
            resolver.receiveMessage(new DNSMessage(serverAddress, responsePacket));
        }

        public void addResponseFor(String serverIpAddress, String queryString, short type, String response) {
            DNSPacket.DNSQuery query = new DNSPacket.DNSQuery(queryString, type, (short) 1);
            DNSPacket.DNSAnswer answer = new DNSPacket.DNSAnswer(query, 0, (short) 0, response);

            DNSSocketAddress serverSocket = new DNSSocketAddress(serverIpAddress, "53");
            if (!responses.containsKey(serverSocket)) {
                responses.put(serverSocket, new HashMap<>());
            }

            final HashMap<DNSPacket.DNSQuery, DNSPacket.DNSAnswer[]> queryHashmap = responses.get(serverSocket);
            queryHashmap.put(query, new DNSPacket.DNSAnswer[]{answer});
        }


        public void addResponseFor(
                String serverIpAddress, String queryString, String authoritativeNameServerAddress, String additionalAnswerAddress
        ) {
            DNSPacket.DNSQuery queryNS = new DNSPacket.DNSQuery(queryString, DNSPacket.TYPE_NS, DNSPacket.CLASS_1);
            DNSPacket.DNSQuery queryAdditional = new DNSPacket.DNSQuery(authoritativeNameServerAddress, DNSPacket.TYPE_A, DNSPacket.CLASS_1);
            DNSPacket.DNSAnswer authorizedNS = new DNSPacket.DNSAnswer(queryNS, 0, (short) 0, authoritativeNameServerAddress);
            DNSPacket.DNSAnswer additionalAnswer = new DNSPacket.DNSAnswer(queryAdditional, 0, (short) 0, additionalAnswerAddress);

            DNSSocketAddress serverSocket = new DNSSocketAddress(serverIpAddress, "53");
            if (!responses.containsKey(serverSocket)) {
                responses.put(serverSocket, new HashMap<>());
            }

            final HashMap<DNSPacket.DNSQuery, DNSPacket.DNSAnswer[]> queryHashmap = responses.get(serverSocket);
            queryHashmap.put(queryNS, new DNSPacket.DNSAnswer[]{authorizedNS, additionalAnswer});
            queryHashmap.put(queryAdditional, new DNSPacket.DNSAnswer[]{authorizedNS, additionalAnswer});
        }


        @Override
        public void shutdown() {
            // not implemented
        }

        private DNSPacket buildResponsePacket(
                short id,
                boolean recursion,
                DNSPacket.DNSQuery query,
                DNSPacket.DNSAnswer[] answers,
                DNSPacket.DNSAnswer[] authoritativeNameServers,
                DNSPacket.DNSAnswer[] additional
        ) {
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
                    .setAnswerRRCount((short) answers.length)
                    .setAuthorityRRCount((short) authoritativeNameServers.length)
                    .setAdditionalRRCount((short) additional.length)
                    .setQueries(query)
                    .setAnswers(answers)
                    .setAuthoritativeNameServers(authoritativeNameServers)
                    .setAdditionalAnswers(additional)
                    .build();
        }
    }

    private DNSPacket.DNSAnswer[] toDNSAnswerArray(List<DNSPacket.DNSAnswer> answers) {
        return answers.toArray(new DNSPacket.DNSAnswer[0]);
    }

}