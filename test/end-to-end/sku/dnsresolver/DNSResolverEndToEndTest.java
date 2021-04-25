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

    // TODO:
    @Test
    public void resolvesDomainName() throws Exception {
        application.resolve("example.com", fakeDnsServer);
        fakeDnsServer.hasReceivedPacket("example.com");
//        fakeDnsServer.respondWith("127.0.0.1");
        application.hasReceivedResponse("127.0.0.1");
    }
}
