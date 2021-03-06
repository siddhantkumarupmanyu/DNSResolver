package sku.dnsresolver;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import sku.dnsresolver.network.DNSSocketAddress;

public final class DNSMessage {

    final DNSSocketAddress from;
    final DNSPacket packet;

    public DNSMessage(DNSSocketAddress from, DNSPacket packet) {
        this.from = from;
        this.packet = packet;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        DNSMessage that = (DNSMessage) obj;

        return new EqualsBuilder()
                .append(from, that.from)
                .append(packet, that.packet)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(from)
                .append(packet)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .toString();
    }
}
