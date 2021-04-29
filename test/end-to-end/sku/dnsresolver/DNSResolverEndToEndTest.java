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
        application.stop();
    }

    @Test
    public void resolvesDomainNameWithRecursion() throws Exception {
        application.resolve("www.example.com", fakeDnsServer);
        fakeDnsServer.hasReceivedPacket(SamplePackets.QUERY_WWW_EXAMPLE_COM);
        fakeDnsServer.respondWith(SamplePackets.RESPONSE_WWW_EXAMPLE_COM);
        application.hasReceivedResponseWith("127.0.0.1");
    }

    @Test
    public void resolvesDomainNameWithCNAME() throws Exception {
        application.resolve("cname.example.com", fakeDnsServer);
        fakeDnsServer.hasReceivedPacket(SamplePackets.QUERY_CNAME_EXAMPLE_COM);
        fakeDnsServer.respondWith(SamplePackets.RESPONSE_CNAME_EXAMPLE_COM);
        application.hasReceivedResponseWith("127.0.0.2");
    }

}
