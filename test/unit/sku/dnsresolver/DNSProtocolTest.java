package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DNSProtocolTest {

    @Test
    public void testToString() {
        DNSProtocol protocol = new DNSProtocol("message");

        assertThat(protocol, hasToString("DNSProtocol[message=message]"));
    }

    @Test
    public void valueObject() {
        DNSProtocol protocol1a = new DNSProtocol("message");
        DNSProtocol protocol1b = new DNSProtocol("message");
        DNSProtocol protocol2 = new DNSProtocol("message2");

        assertThat("same object", protocol1a, is(equalTo(protocol1a)));
        assertThat("same message", protocol1a, is(equalTo(protocol1b)));
        assertThat("different message", protocol2, is(not(equalTo(protocol1a))));
    }

}