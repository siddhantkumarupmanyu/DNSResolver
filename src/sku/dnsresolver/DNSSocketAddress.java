package sku.dnsresolver;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public final class DNSSocketAddress {

    public final String ipAddress;
    public final String port;

    public DNSSocketAddress(String ipAddress, String port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public final InetSocketAddress socketAddress() throws UnknownHostException {
        int port = Integer.parseInt(this.port);
        return new InetSocketAddress(ipAddress, port);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        DNSSocketAddress that = (DNSSocketAddress) obj;

        return new EqualsBuilder()
                .append(ipAddress, that.ipAddress)
                .append(port, that.port)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(ipAddress)
                .append(port)
                .toHashCode();
    }
}
