package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DNSExchangeConverterTest {

//    @Test
//    public void sendPacketInReal() throws IOException {
////     TODO: remove this
//
//        DatagramSocket socket = new DatagramSocket();
//        DNSSocketAddress socketAddress = new DNSSocketAddress("192.168.0.1", "53");
//
//        byte[] request = DNSExchangeParser.toBytes(null);
//
//        DatagramPacket packet = new DatagramPacket(request, request.length, socketAddress.inetSocketAddress());
//        socket.send(packet);
//    }

//
//    // TODO: add support for parsing response too
//    byte[] response = {0x00, 0x00, 0x00, 0x01, (byte) 0x81, (byte) 0x80, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x03, 0x77, 0x77, 0x77,
//            0x00, 0x10, 0x06, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, 0x03, 0x63, 0x6f, 0x6d, 0x00, 0x00, 0x01, 0x00, 0x01,
//            0x00, 0x20, (byte) 0xc0, 0x0c, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x67, 0x00, 0x04, (byte) 0xac, (byte) 0xd9, (byte) 0xa0, (byte) 0xc4};

}