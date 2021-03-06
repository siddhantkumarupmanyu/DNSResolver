package sku.dnsresolver;

public class DNSPacketBuilder {
    private short id;
    private boolean response;
    private int opCode;
    private boolean authoritative;
    private boolean truncated;
    private boolean recursionDesired;
    private boolean recursionAvailable;
    private boolean z;
    private boolean answerAuthenticated;
    private boolean nonAuthenticatedData;
    private int replyCode;
    private short questionCount;
    private short answerRRCount;
    private short authorityRRCount;
    private short additionalRRCount;

    private DNSPacket.DNSQuery[] queries = new DNSPacket.DNSQuery[0];
    private DNSPacket.DNSAnswer[] answers = new DNSPacket.DNSAnswer[0];
    private DNSPacket.DNSAnswer[] authoritativeNameServers = new DNSPacket.DNSAnswer[0];
    private DNSPacket.DNSAnswer[] additionalAnswers = new DNSPacket.DNSAnswer[0];

    public DNSPacketBuilder setId(short id) {
        this.id = id;
        return this;
    }

    public DNSPacketBuilder setResponse(boolean response) {
        this.response = response;
        return this;
    }

    public DNSPacketBuilder setOpCode(int opCode) {
        this.opCode = opCode;
        return this;
    }

    public DNSPacketBuilder setAuthoritative(boolean authoritative) {
        this.authoritative = authoritative;
        return this;
    }

    public DNSPacketBuilder setTruncated(boolean truncated) {
        this.truncated = truncated;
        return this;
    }

    public DNSPacketBuilder setRecursionDesired(boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
        return this;
    }

    public DNSPacketBuilder setRecursionAvailable(boolean recursionAvailable) {
        this.recursionAvailable = recursionAvailable;
        return this;
    }

    public DNSPacketBuilder setZ(boolean z) {
        this.z = z;
        return this;
    }

    public DNSPacketBuilder setAnswerAuthenticated(boolean answerAuthenticated) {
        this.answerAuthenticated = answerAuthenticated;
        return this;
    }

    public DNSPacketBuilder setNonAuthenticatedData(boolean nonAuthenticatedData) {
        this.nonAuthenticatedData = nonAuthenticatedData;
        return this;
    }

    public DNSPacketBuilder setReplyCode(int replyCode) {
        this.replyCode = replyCode;
        return this;
    }

    public DNSPacketBuilder setQuestionCount(short questionCount) {
        this.questionCount = questionCount;
        return this;
    }

    public DNSPacketBuilder setAnswerRRCount(short answerRRCount) {
        this.answerRRCount = answerRRCount;
        return this;
    }

    public DNSPacketBuilder setAuthorityRRCount(short authorityRRCount) {
        this.authorityRRCount = authorityRRCount;
        return this;
    }

    public DNSPacketBuilder setAdditionalRRCount(short additionalRRCount) {
        this.additionalRRCount = additionalRRCount;
        return this;
    }

    public DNSPacketBuilder setQueries(DNSPacket.DNSQuery... queries) {
        this.queries = queries;
        return this;
    }

    public DNSPacketBuilder setAnswers(DNSPacket.DNSAnswer... answers) {
        this.answers = answers;
        return this;
    }

    public DNSPacketBuilder setAuthoritativeNameServers(DNSPacket.DNSAnswer... authoritativeNameServers) {
        this.authoritativeNameServers = authoritativeNameServers;
        return this;
    }

    public DNSPacketBuilder setAdditionalAnswers(DNSPacket.DNSAnswer... additionalAnswers) {
        this.additionalAnswers = additionalAnswers;
        return this;
    }

    public DNSPacket build() {
        return new DNSPacket(
                id,
                response,
                opCode,
                authoritative,
                truncated,
                recursionDesired,
                recursionAvailable,
                z,
                answerAuthenticated,
                nonAuthenticatedData,
                replyCode,
                questionCount,
                answerRRCount,
                authorityRRCount,
                additionalRRCount,
                queries,
                answers,
                authoritativeNameServers,
                additionalAnswers
        );
    }
}
