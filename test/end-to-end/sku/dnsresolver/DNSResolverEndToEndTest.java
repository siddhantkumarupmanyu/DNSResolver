package sku.dnsresolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DNSResolverEndToEndTest {

    private final FakeDnsServer fakeDnsServer = new FakeDnsServer();
    private final ApplicationRunner application = new ApplicationRunner();

    @Before
    public void setUp() throws Exception {
        fakeDnsServer.startServer();
    }

    @After
    public void tearDown() {
        fakeDnsServer.stopServer();
    }

    @Test
    public void resolvesDomainNameWithRecursion() throws Exception {
        application.resolve("www.example.com", fakeDnsServer);
        fakeDnsServer.hasReceivedPacket(SamplePackets.QUERY_WWW_EXAMPLE_COM);
        fakeDnsServer.respondWith(SamplePackets.RESPONSE_WWW_EXAMPLE_COM);
        application.hasReceivedResponseWith("127.0.0.1");
    }

    @Test
    public void resolvesDomainNameWithCNAME() {

    }

    // 000181800001000200000000037777770866616365626f6f6b03636f6d0000010001c00c0005000100000408001109737461722d6d696e690463313072c010c02e000100010000000e00049df01423

}
