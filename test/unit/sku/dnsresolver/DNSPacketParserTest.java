package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DNSPacketParserTest {


    // TODO

    @Test
    public void parseWhenResponseIsOfGoogle() {
        DNSExchange expected = new DNSExchange.DNSExchangeBuilder()
                .setId((short) 1)
                .setQuery("www.google.com")
                .setRecursion(true)
                .build2();

        byte[] response = googleResponsePacket();

        DNSPacketParser parser = new DNSPacketParser(response);

        assertThat(parser.getDNSExchange(), is(equalTo(expected)));
    }

    private byte[] googleResponsePacket() {
        return new byte[]{
                // header
                0x00, 0x01, // id
                (byte) 0x81, // QR = 1, OP Code = 0000, AA = 0, TC = 0, RD = 1
                (byte) 0x80, // RA = 1, Z = 0, 0, 0, RCode = 0000
                0x00, 0x01, // QDCount
                0x00, 0x01, // ANCount
                0x00, 0x00, // NSCount
                0x00, 0x00, // ARCount
                // body
                //      query
                0x03, 0x77, 0x77, 0x77, // 3 www
                0x06, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, // 6 google
                0x03, 0x63, 0x6f, 0x6d, // 3 com
                0x00, // null
                0x00, 0x01, // QType
                0x00, 0x01, // QClass
                //      answer
                (byte) 0xc0, 0x0c, // Name, used pointer offset = 0x0c
                0x00, 0x01, // Type
                0x00, 0x01, // Class
                0x00, 0x00, 0x00, 0x73, // TTL
                0x00, 0x04, // RDLength
                (byte) 0xac, (byte) 0xd9, (byte) 0xa0, (byte) 0xc4 // RData
        };
    }

}