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
    public void resolvesDomainName() throws Exception {
        application.resolve("example.com", fakeDnsServer);
        fakeDnsServer.hasReceivedPacket("example.com");
        fakeDnsServer.respondWith(address_127_0_0_1_inBytes());
        application.hasReceivedResponseWith("127.0.0.1");
    }

    private byte[] address_127_0_0_1_inBytes() {
        return new byte[]{0x7f, 0x00, 0x00, 0x01};
    }
}
