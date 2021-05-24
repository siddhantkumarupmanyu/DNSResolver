package sku.dnsresolver.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sku.dnsresolver.FakeDnsServer;

// this is a integration test
public class DatagramFactoryTest {
    public static final int TIMEOUT_IN_MILLISECONDS = 250;

    private final FakeDnsServer fakeDnsServer = new FakeDnsServer();
    private final DNSSocketAddress fakeServerAddress = new DNSSocketAddress(fakeDnsServer.ipAddress(), fakeDnsServer.port());

    private final PacketTransceiverFactory factory = new DatagramFactory();

    @Before
    public void setUp() throws Exception {
        fakeDnsServer.startServer();
    }

    @After
    public void tearDown() {
        fakeDnsServer.stopServer();
    }

    @Test(expected = TimeoutException.class)
    public void timeout() throws TimeoutException, InterruptedException {
        factory.setTimeout(TIMEOUT_IN_MILLISECONDS);

        DatagramPacketTransceiver transceiver = (DatagramPacketTransceiver) factory.createTransceiver();

        byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04};
        PacketTransceiver.Packet packet = new PacketTransceiver.Packet(fakeServerAddress.inetSocketAddress(), data);

        transceiver.sendPacket(packet);
        fakeDnsServer.hasReceivedPacket(data);

        transceiver.receivePacket();
    }

}