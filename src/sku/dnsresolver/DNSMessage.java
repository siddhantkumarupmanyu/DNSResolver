package sku.dnsresolver;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class DNSMessage {

    private final DNSSocketAddress socketAddress;
    private final DNSProtocol protocol;

    public DNSMessage(DNSSocketAddress socketAddress, DNSProtocol protocol) {
        this.socketAddress = socketAddress;
        this.protocol = protocol;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        DNSMessage that = (DNSMessage) obj;

        return new EqualsBuilder()
                .append(socketAddress, that.socketAddress)
                .append(protocol, that.protocol)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(socketAddress)
                .append(protocol)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .toString();
    }
}
