package sku.dnsresolver;

import org.junit.Test;

import java.net.InetSocketAddress;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class DNSSocketAddressTest {

    @Test
    public void socketAddressFromIpAndPort() throws Exception {
        DNSSocketAddress server = new DNSSocketAddress("127.0.0.1", "5000");
        InetSocketAddress socketAddress = server.socketAddress();

        assertFalse("address should be resolved", socketAddress.isUnresolved());
        assertThat("hostname/ipaddress:port", socketAddress.toString(), is(equalTo("/127.0.0.1:5000")));
    }

    @Test
    public void valueObject() {
        DNSSocketAddress add1a = new DNSSocketAddress("127.0.0.1", "5000");
        DNSSocketAddress add1b = new DNSSocketAddress("127.0.0.1", "5000");
        DNSSocketAddress add2 = new DNSSocketAddress("10.0.0.0", "5000");
        DNSSocketAddress add3 = new DNSSocketAddress("10.0.0.0", "4000");

        assertThat("same ip and port", add1a, is(equalTo(add1b)));
        assertThat("different ip same port", add2, is(not(equalTo(add1a))));
        assertThat("same ip different port", add2, is(not(equalTo(add3))));
    }
}