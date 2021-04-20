package sku.dnsresolver;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.nio.charset.StandardCharsets;

public class DNSExchange {

    final String message;
    final short id;
    final boolean recursion;
    final String question;

    public DNSExchange(String message) {
        this.message = message;
        this.id = 0;
        this.recursion = false;
        this.question = null;
    }

    public DNSExchange(short id, boolean recursion, String question) {
        this.message = "message";
        this.id = id;
        this.recursion = recursion;
        this.question = question;
    }

    public byte[] getBytes() {
        return message.getBytes(StandardCharsets.UTF_8);
    }

    public static DNSExchange from(byte[] bytes) {
        return new DNSExchangeBuilder()
                .withMessage(new String(bytes, 0, bytes.length, StandardCharsets.UTF_8))
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        DNSExchange that = (DNSExchange) obj;

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

    public static class DNSExchangeBuilder {
        private String message;
        private short id;
        private boolean recursion;
        private String question;

        public DNSExchangeBuilder() {
        }

        public DNSExchangeBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public DNSExchangeBuilder setId(short id) {
            this.id = id;
            return this;
        }

        public DNSExchangeBuilder setRecursion(boolean recursion) {
            this.recursion = recursion;
            return this;
        }

        public DNSExchangeBuilder setQuestion(String question) {
            this.question = question;
            return this;
        }

        public DNSExchange build() {
            return new DNSExchange(this.message);
        }

        public DNSExchange build2() {
            return new DNSExchange(id, recursion, question);
        }
    }
}
