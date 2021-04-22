package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DNSPacketGeneratorTest {

    @Test
    public void generateBytesWhenQueryIsGoogle() {
        byte[] expected = {
                // header
                0x00, 0xA, // id
                0x01, // QR, OP Code, AA, TC, RD = 1
                0x00, // RA, Z, RCode
                0x00, 0x01, // QDCount
                0x00, 0x00, // ANCount
                0x00, 0x00, // NSCount
                0x00, 0x00, // ARCount
                // body
                0x03, 0x77, 0x77, 0x77, // 3 www
                0x06, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, // 6 google
                0x03, 0x63, 0x6f, 0x6d, // 3 com
                0x00, // null label or termination
                0x00, 0x01, // QType
                0x00, 0x01, // QClass
        };

        DNSExchange exchange = new DNSExchange.DNSExchangeBuilder()
                .setId((short) 10)
                .setQuestion("www.google.com")
                .setRecursion(true)
                .build2();

        byte[] actual = new DNSPacketGenerator(exchange).getBytes();
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void generateBytesWhenQueryIsExample() {
        byte[] expected = {
                // header
                0x6d, 0x60, // id
                0x00, // QR, OP Code, AA, TC, RD = 0
                0x00, // RA, Z, RCode
                0x00, 0x01, // QDCount
                0x00, 0x00, // ANCount
                0x00, 0x00, // NSCount
                0x00, 0x00, // ARCount
                // body
                0x04, 0x65, 0x64, 0x67, 0x65, // 4 edge
                0x07, 0x65, 0x78, 0x61, 0x6d, 0x70, 0x6c, 0x65, // 7 edge.example.com
                0x03, 0x63, 0x6f, 0x6d, // 3 com
                0x00, // null label or termination
                0x00, 0x01, // QType
                0x00, 0x01, // QClass
        };

        DNSExchange exchange = new DNSExchange.DNSExchangeBuilder()
                .setId((short) 28000)
                .setQuestion("edge.example.com")
                .setRecursion(false)
                .build2();

        byte[] actual = new DNSPacketGenerator(exchange).getBytes();
        assertThat(actual, is(equalTo(expected)));
    }

}