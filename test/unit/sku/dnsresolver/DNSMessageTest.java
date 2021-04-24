package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DNSMessageTest {

    private final DNSSocketAddress socketAddress1 = new DNSSocketAddress("127.0.0.1", "5000");
    private final DNSSocketAddress socketAddress2 = new DNSSocketAddress("127.0.0.1", "6000");

    private final DNSPacket packet1 = new DNSQueryBuilder()
            .setId((short) 1)
            .setRecursionDesired(true)
            .setQueries(new DNSPacket.DNSQuery("www.example.com", (short) 1, (short) 1))
            .build();

    private final DNSPacket packet2 = new DNSQueryBuilder()
            .setId((short) 2)
            .setRecursionDesired(false)
            .setQueries(new DNSPacket.DNSQuery("www.example.com", (short) 1, (short) 1))
            .build();


    @Test
    public void testToString() {
        DNSMessage message = new DNSMessage(socketAddress1, packet1);
        assertThat(message, hasToString("DNSMessage[from=" + socketAddress1 + ",exchange=" + packet1 + "]"));
    }


    @Test
    public void valueObject() {
        DNSMessage message1a = new DNSMessage(socketAddress1, packet1);
        DNSMessage message1b = new DNSMessage(socketAddress1, packet1);
        DNSMessage message2 = new DNSMessage(socketAddress1, packet2);
        DNSMessage message3 = new DNSMessage(socketAddress2, packet1);

        assertThat("same socket address and protocol", message1a, is(equalTo(message1b)));
        assertThat("same socket address, different protocol", message2, is(not(equalTo(message1a))));
        assertThat("same protocol, different socket address", message3, is(not(equalTo(message1a))));
        assertThat("different socket address, different protocol", message2, is(not(equalTo(message3))));
    }
}