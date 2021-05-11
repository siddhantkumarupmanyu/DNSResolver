package sku.dnsresolver;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public final class DNSPacket {
    public static final short TYPE_A = 1;
    public static final short TYPE_NS = 2;
    public static final short CLASS_1 = 1;


    // header
    public final short id; // 16 bit
    public final boolean response; // 1 bit
    public final int opCode; // 4 bit
    public final boolean authoritative; // 1 bit
    public final boolean truncated; // 1 bit
    public final boolean recursionDesired; // 1 bit
    public final boolean recursionAvailable; // 1 bit
    public final boolean z; // 1 bit
    public final boolean answerAuthenticated; // 1 bit
    public final boolean nonAuthenticatedData; // 1 bit
    public final int replyCode; // 4 bit
    public final short questionCount; // 16 bit
    public final short answerRRCount; // 16 bit
    public final short authorityRRCount; // 16 bit
    public final short additionalRRCount; // 16 bit

    //body
    public final DNSQuery[] queries;
    public final DNSAnswer[] answers;

    public DNSPacket(
            short id,
            boolean response,
            int opCode,
            boolean authoritative,
            boolean truncated,
            boolean recursionDesired,
            boolean recursionAvailable,
            boolean z,
            boolean answerAuthenticated,
            boolean nonAuthenticatedData,
            int replyCode,
            short questionCount,
            short answerRRCount,
            short authorityRRCount,
            short additionalRRCount,
            DNSQuery[] queries,
            DNSAnswer[] answers
    ) {
        this.id = id;
        this.response = response;
        this.opCode = opCode;
        this.authoritative = authoritative;
        this.truncated = truncated;
        this.recursionDesired = recursionDesired;
        this.recursionAvailable = recursionAvailable;
        this.z = z;
        this.answerAuthenticated = answerAuthenticated;
        this.nonAuthenticatedData = nonAuthenticatedData;
        this.replyCode = replyCode;
        this.questionCount = questionCount;
        this.answerRRCount = answerRRCount;
        this.authorityRRCount = authorityRRCount;
        this.additionalRRCount = additionalRRCount;
        this.queries = queries;
        this.answers = answers;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        DNSPacket that = (DNSPacket) obj;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(response, that.response)
                .append(opCode, that.opCode)
                .append(authoritative, that.authoritative)
                .append(truncated, that.truncated)
                .append(recursionDesired, that.recursionDesired)
                .append(recursionAvailable, that.recursionAvailable)
                .append(z, that.z)
                .append(answerAuthenticated, that.answerAuthenticated)
                .append(nonAuthenticatedData, that.nonAuthenticatedData)
                .append(replyCode, that.replyCode)
                .append(questionCount, that.questionCount)
                .append(answerRRCount, that.answerRRCount)
                .append(authorityRRCount, that.authorityRRCount)
                .append(additionalRRCount, that.additionalRRCount)
                .append(queries, that.queries)
                .append(answers, that.answers)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(response)
                .append(opCode)
                .append(authoritative)
                .append(truncated)
                .append(recursionDesired)
                .append(recursionAvailable)
                .append(z)
                .append(answerAuthenticated)
                .append(nonAuthenticatedData)
                .append(replyCode)
                .append(questionCount)
                .append(answerRRCount)
                .append(authorityRRCount)
                .append(additionalRRCount)
                .append(queries)
                .append(answers)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .toString();
    }

    public static class DNSQuery {
        public final String query; // variable length
        public final short qType; // 16 bit
        public final short qClass; // 16 bit

        public DNSQuery(String query, short qType, short qClass) {
            this.query = query;
            this.qType = qType;
            this.qClass = qClass;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;

            if (obj == null || getClass() != obj.getClass()) return false;

            DNSQuery that = (DNSQuery) obj;

            return new EqualsBuilder()
                    .append(qType, that.qType)
                    .append(qClass, that.qClass)
                    .append(query, that.query)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(query)
                    .append(qType)
                    .append(qClass)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .toString();
        }
    }

    public static class DNSAnswer {
        public final DNSQuery query;
        public final int timeToLive; // 32 bits
        public final short dataLength; // 16 bits
        public final String address; // variable - depends on dataLength

        public DNSAnswer(DNSQuery query, int timeToLive, short dataLength, String address) {
            this.query = query;
            this.timeToLive = timeToLive;
            this.dataLength = dataLength;
            this.address = address;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;

            if (obj == null || getClass() != obj.getClass()) return false;

            DNSAnswer that = (DNSAnswer) obj;

            return new EqualsBuilder()
                    .append(timeToLive, that.timeToLive)
                    .append(dataLength, that.dataLength)
                    .append(address, that.address)
                    .append(query, that.query)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(query)
                    .append(timeToLive)
                    .append(dataLength)
                    .append(address)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .toString();
        }
    }

}
