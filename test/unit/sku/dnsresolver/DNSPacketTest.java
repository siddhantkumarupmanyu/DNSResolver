package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DNSPacketTest {

    @Test
    public void getReadableIpAddressFromDNSAnswer() {
        DNSPacket packet = dnsPacketWithId(0);
        assertThat(packet.answers[0].readableAddress(), is(equalTo("127.0.0.1")));
    }

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
        DNSPacket.DNSAnswer answer = new DNSPacket.DNSAnswer(query, 256, (short) 4, address_127_0_0_1_inBytes());
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
                (short) 0,
                (short) 0,
                (short) 0,
                new DNSPacket.DNSQuery[]{query},
                new DNSPacket.DNSAnswer[]{answer}
        );
    }

    private int address_127_0_0_1_inBytes() {
        return 0x7f000001;
    }
}