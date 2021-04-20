package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DNSMessageTest {

    private final DNSSocketAddress SOCKET_ADDRESS_1 = new DNSSocketAddress("127.0.0.1", "5000");
    private final DNSSocketAddress SOCKET_ADDRESS_2 = new DNSSocketAddress("127.0.0.1", "6000");

    private final DNSExchange PROTOCOL_1 = new DNSExchange("message");
    private final DNSExchange PROTOCOL_2 = new DNSExchange("message2");


    @Test
    public void testToString() {
        DNSMessage message = new DNSMessage(SOCKET_ADDRESS_1, PROTOCOL_1);
        assertThat(message, hasToString("DNSMessage[from=" + SOCKET_ADDRESS_1 + ",protocol=" + PROTOCOL_1 + "]"));
    }


    @Test
    public void valueObject() {
        DNSMessage message1a = new DNSMessage(SOCKET_ADDRESS_1, PROTOCOL_1);
        DNSMessage message1b = new DNSMessage(SOCKET_ADDRESS_1, PROTOCOL_1);
        DNSMessage message2 = new DNSMessage(SOCKET_ADDRESS_1, PROTOCOL_2);
        DNSMessage message3 = new DNSMessage(SOCKET_ADDRESS_2, PROTOCOL_1);

        assertThat("same socket address and protocol", message1a, is(equalTo(message1b)));
        assertThat("same socket address, different protocol", message2, is(not(equalTo(message1a))));
        assertThat("same protocol, different socket address", message3, is(not(equalTo(message1a))));
        assertThat("different socket address, different protocol", message2, is(not(equalTo(message3))));
    }
}