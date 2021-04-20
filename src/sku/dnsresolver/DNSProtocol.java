package sku.dnsresolver;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.nio.charset.StandardCharsets;

public class DNSProtocol {

    final String message;

    public DNSProtocol(String message) {
        this.message = message;
    }

    public byte[] getBytes() {
        return message.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        DNSProtocol that = (DNSProtocol) obj;

        return new EqualsBuilder()
                .append(message, that.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(message)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .toString();
    }

    public static DNSProtocol from(byte[] bytes) {
        return new DNSProtocolBuilder()
                .withMessage(new String(bytes, 0, bytes.length, StandardCharsets.UTF_8))
                .build();
    }

    public static class DNSProtocolBuilder {
        private String message;

        public DNSProtocolBuilder() {
        }

        public DNSProtocolBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public DNSProtocol build() {
            return new DNSProtocol(this.message);
        }
    }
}
