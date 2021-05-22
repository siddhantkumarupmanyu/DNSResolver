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

        fakeExecutor.addAnsResponseFor(serverSocket.ipAddress, query, DNSPacket.TYPE_A, resolvedAddress);

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

        fakeExecutor.addAnsResponseFor("127.0.0.1", "", DNSPacket.TYPE_NS, ROOT_NS_SERVER);
        fakeExecutor.addAnsResponseFor("127.0.0.1", ROOT_NS_SERVER, DNSPacket.TYPE_A, ROOT_NS_SERVER_IP);

        fakeExecutor.addResponseFor(ROOT_NS_SERVER_IP, "com", DNSPacket.TYPE_NS, new TestPair(COM_NS_SERVER, COM_NS_SERVER_IP));

        fakeExecutor.addResponseFor(COM_NS_SERVER_IP, "example.com", DNSPacket.TYPE_NS, new TestPair(EXAMPLE_NS_SERVER, TestPair.NO_ADDITIONAL));
        fakeExecutor.addResponseFor(COM_NS_SERVER_IP, EXAMPLE_NS_SERVER, DNSPacket.TYPE_A, new TestPair(EXAMPLE_NS_SERVER, EXAMPLE_NS_SERVER_IP));

        fakeExecutor.addAuthoritativeResponse(EXAMPLE_NS_SERVER_IP, "www.example.com");
        fakeExecutor.addAnsResponseFor(EXAMPLE_NS_SERVER_IP, "www.example.com", DNSPacket.TYPE_A, WWW_EXAMPLE_COM_IP);

        context.checking(new Expectations() {{
            oneOf(uiListener).responseText(with(allOf(
                    containsString("label: " + "www.example.com"),
                    containsString("address: " + WWW_EXAMPLE_COM_IP)
            )));
        }});

        resolver.resolve("www.example.com", "127.0.0.1", "53", false);
    }

    @Test
    public void additionalSectionDoesNotContainFirstAuthoritativeNSAddress() {
        // see RESPONSE_EXAMPLE_NS_IP_ADDRESS, additional does not contain ns.icann.org address
        // what i should do is to see if i made a query for NS,
        // if so then select that one from additional,
        // else select one which is contained in additional,
        // question is why i am i going in authoritative if additional exist
        // why not select one from there??? because i am not confident that it only contains ns address

        // should i leave checking this to End tests???
    }

    // TODO:
//    @Test
//    case when recursion desired is true but server response have recursion available as false{
    // to make this test possible
    // we can mock Network executor instead of using FakeExecutor.
//    }

    @Test
    public void requestNextNSToSameServer_WhenIntermediateNSIsAuthoritative() {
        String ROOT_NS_SERVER = "a.root-servers.net";
        String ROOT_NS_SERVER_IP = "127.0.0.2";

        String COM_NS_SERVER = "a.gtld-servers.net";
        String COM_NS_SERVER_IP = "127.0.0.3";

        String WWW_EXAMPLE_COM_IP = "192.168.0.1";

        fakeExecutor.addAnsResponseFor("127.0.0.1", "", DNSPacket.TYPE_NS, ROOT_NS_SERVER);
        fakeExecutor.addAnsResponseFor("127.0.0.1", ROOT_NS_SERVER, DNSPacket.TYPE_A, ROOT_NS_SERVER_IP);

        fakeExecutor.addResponseFor(ROOT_NS_SERVER_IP, "com", DNSPacket.TYPE_NS, new TestPair(COM_NS_SERVER, COM_NS_SERVER_IP));

        fakeExecutor.addAuthoritativeResponse(COM_NS_SERVER_IP, "example.com");

        fakeExecutor.addAuthoritativeResponse(COM_NS_SERVER_IP, "www.example.com");
        fakeExecutor.addAnsResponseFor(COM_NS_SERVER_IP, "www.example.com", DNSPacket.TYPE_A, WWW_EXAMPLE_COM_IP);

        context.checking(new Expectations() {{
            oneOf(uiListener).responseText(with(allOf(
                    containsString("label: " + "www.example.com"),
                    containsString("address: " + WWW_EXAMPLE_COM_IP)
            )));
        }});

        resolver.resolve("www.example.com", "127.0.0.1", "53", false);
    }

    private class FakeExecutor implements NetworkExecutor {

        private final HashMap<DNSSocketAddress, HashMap<DNSPacket.DNSQuery, List<DNSPacket.DNSAnswer[]>>> responses = new HashMap<>();

        @Override
        public void query(DNSSocketAddress serverAddress, DNSPacket packet) {
            final DNSPacket.DNSQuery askedQuery = packet.queries[0];

            final HashMap<DNSPacket.DNSQuery, List<DNSPacket.DNSAnswer[]>> queryHashmap = responses.get(serverAddress);

            List<DNSPacket.DNSAnswer[]> answers = queryHashmap.get(askedQuery);

            final DNSPacket.DNSAnswer[] answersSection = answers.get(0);
            final DNSPacket.DNSAnswer[] authoritativeNameServes = answers.get(1);
            final DNSPacket.DNSAnswer[] additionalSection = answers.get(2);
            boolean authority =
                    (answersSection.length == 0) && (authoritativeNameServes.length == 0) && (additionalSection.length == 0);

            DNSPacket responsePacket = buildResponsePacket(
                    packet.id, packet.recursionDesired, authority, askedQuery,
                    answersSection, authoritativeNameServes, additionalSection);
            resolver.receiveMessage(new DNSMessage(serverAddress, responsePacket));
        }

        public void addAnsResponseFor(String serverIpAddress, String queryString, short type, String response) {
            DNSPacket.DNSQuery query = new DNSPacket.DNSQuery(queryString, type, DNSPacket.CLASS_1);
            DNSPacket.DNSAnswer answer = new DNSPacket.DNSAnswer(query, 0, (short) 0, response);

            DNSPacket.DNSAnswer[] answers = {answer};
            DNSPacket.DNSAnswer[] authoritative = new DNSPacket.DNSAnswer[0];
            DNSPacket.DNSAnswer[] additional = new DNSPacket.DNSAnswer[0];

            addResponse(serverIpAddress, query, answers, authoritative, additional);
        }

        public void addResponseFor(
                String serverIpAddress, String queryString, short type, TestPair... pairs) {
            DNSPacket.DNSQuery queryNS = new DNSPacket.DNSQuery(queryString, type, DNSPacket.CLASS_1);

            List<DNSPacket.DNSAnswer> authoritativeNSList = new ArrayList<>();
            List<DNSPacket.DNSAnswer> additionalList = new ArrayList<>();

            for (TestPair pair : pairs) {
                String authoritativeNSAddress = pair.authoritativeNameServerAddress;
                authoritativeNSList.add(new DNSPacket.DNSAnswer(queryNS, 0, (short) 0, authoritativeNSAddress));

                String additional = pair.additional;
                if (!additional.equals(TestPair.NO_ADDITIONAL)) {
                    DNSPacket.DNSQuery queryAdditional = new DNSPacket.DNSQuery(authoritativeNSAddress, DNSPacket.TYPE_A, DNSPacket.CLASS_1);
                    additionalList.add(new DNSPacket.DNSAnswer(queryAdditional, 0, (short) 0, additional));
                }
            }

            DNSPacket.DNSAnswer[] answers = new DNSPacket.DNSAnswer[0];
            DNSPacket.DNSAnswer[] authoritative = authoritativeNSList.toArray(new DNSPacket.DNSAnswer[0]);
            DNSPacket.DNSAnswer[] additional = additionalList.toArray(new DNSPacket.DNSAnswer[0]);

            addResponse(serverIpAddress, queryNS, answers, authoritative, additional);
        }

        public void addAuthoritativeResponse(String serverIpAddress, String queryString) {
            DNSPacket.DNSQuery queryNS = new DNSPacket.DNSQuery(queryString, DNSPacket.TYPE_NS, DNSPacket.CLASS_1);

            // too keep it simple; not adding SOA in response
            // see RESPONSE_WWW_EXAMPLE_NS for more details
            addResponse(serverIpAddress, queryNS,
                    new DNSPacket.DNSAnswer[0], new DNSPacket.DNSAnswer[0], new DNSPacket.DNSAnswer[0]);
        }

        private void addResponse(
                String serverIpAddress, DNSPacket.DNSQuery query,
                DNSPacket.DNSAnswer[] answer, DNSPacket.DNSAnswer[] authoritative, DNSPacket.DNSAnswer[] additional
        ) {
            List<DNSPacket.DNSAnswer[]> list = new ArrayList<>(3);
            list.add(answer);
            list.add(authoritative);
            list.add(additional);

            DNSSocketAddress serverSocket = new DNSSocketAddress(serverIpAddress, "53");
            if (!responses.containsKey(serverSocket)) {
                responses.put(serverSocket, new HashMap<>());
            }

            final HashMap<DNSPacket.DNSQuery, List<DNSPacket.DNSAnswer[]>> queryHashmap = responses.get(serverSocket);
            queryHashmap.put(query, list);
        }


        @Override
        public void addListener(DNSMessageListener listener) {
            // not implemented
        }

        @Override
        public void shutdown() {
            // not implemented
        }

    }

    private DNSPacket buildResponsePacket(
            short id, boolean recursion, boolean authority,
            DNSPacket.DNSQuery query,
            DNSPacket.DNSAnswer[] answers,
            DNSPacket.DNSAnswer[] authoritativeNameServers,
            DNSPacket.DNSAnswer[] additional
    ) {
        return new DNSPacketBuilder()
                .setId(id)
                .setResponse(true)
                .setOpCode(0)
                .setAuthoritative(authority)
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

    private static class TestPair {
        static final String NO_ADDITIONAL = "";

        final String authoritativeNameServerAddress;
        final String additional;

        public TestPair(String authoritativeNameServerAddress, String additionalAddress) {
            this.authoritativeNameServerAddress = authoritativeNameServerAddress;
            this.additional = additionalAddress;
        }
    }

}

// https://stackoverflow.com/questions/12211030/capturing-method-parameter-in-jmock-to-pass-to-a-stubbed-implementation