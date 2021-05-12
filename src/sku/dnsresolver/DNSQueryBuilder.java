package sku.dnsresolver;

public class DNSQueryBuilder {

    private short id;
    private boolean recursionDesired;
    private DNSPacket.DNSQuery[] queries;

    public DNSQueryBuilder setId(short id) {
        this.id = id;
        return this;
    }

    public DNSQueryBuilder setRecursionDesired(boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
        return this;
    }

    public DNSQueryBuilder setQueries(DNSPacket.DNSQuery... queries) {
        this.queries = queries;
        return this;
    }

    public DNSPacket build() {
        return new DNSPacketBuilder()
                .setId(id)
                .setResponse(false)
                .setOpCode(0)
                .setAuthoritative(false)
                .setTruncated(false)
                .setRecursionDesired(recursionDesired)
                .setRecursionAvailable(false)
                .setZ(false)
                .setAnswerAuthenticated(false)
                .setNonAuthenticatedData(false)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 0)
                .setAuthorityRRCount((short) 0)
                .setAdditionalRRCount((short) 0)
                .setReplyCode(0)
                .setQueries(queries)
                .setAnswers()
                .setAuthoritativeNameServers()
                .build();
    }
}
