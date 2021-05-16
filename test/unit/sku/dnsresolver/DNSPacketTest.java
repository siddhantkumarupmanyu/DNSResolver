package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DNSPacketTest {

    @Test
    public void valueObject() {
        DNSPacket packet1a = dnsPacketWithId(0);
        DNSPacket packet1b = dnsPacketWithId(0);
        DNSPacket packet2 = dnsPacketWithId(1);

        assertThat("same object", packet1a, is(equalTo(packet1a)));
        assertThat("same packet", packet1a, is(equalTo(packet1b)));
        assertThat("different packet", packet2, is(not(equalTo(packet1a))));
    }

    private DNSPacket dnsPacketWithId(int id) {
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("www.example.com", (short) 1, (short) 1);
        DNSPacket.DNSAnswer answer = new DNSPacket.DNSAnswer(query, 256, (short) 4, "127.0.0.1");
        DNSPacket.DNSAnswer authoritativeNameServer = new DNSPacket.DNSAnswer(query, 256, (short) 4, "ns.example.com");
        DNSPacket.DNSQuery nsQuery = new DNSPacket.DNSQuery("ns.example.com", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSAnswer additional = new DNSPacket.DNSAnswer(nsQuery, 256, (short) 4, "127.0.0.2");

        return new DNSPacket(
                (short) id,
                false,
                0,
                false,
                false,
                true,
                false,
                false,
                false,
                false,
                0,
                (short) 1,
                (short) 1,
                (short) 1,
                (short) 1,
                new DNSPacket.DNSQuery[]{query},
                new DNSPacket.DNSAnswer[]{answer},
                new DNSPacket.DNSAnswer[]{authoritativeNameServer},
                new DNSPacket.DNSAnswer[]{additional}
        );
    }
}