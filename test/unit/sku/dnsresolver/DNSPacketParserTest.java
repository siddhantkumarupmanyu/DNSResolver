package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DNSPacketParserTest {

    @Test
    public void parseWhenResponseIsOfGoogleWithNegativeId() {
        short id = -21332;
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("www.google.com", (short) 1, (short) 1);
        DNSPacket.DNSAnswer answer = new DNSPacket.DNSAnswer(query, 115, (short) 4, "172.217.160.196");
        DNSPacket packet = new DNSPacketBuilder()
                .setId(id)
                .setResponse(true)
                .setOpCode(0)
                .setAuthoritative(false)
                .setTruncated(false)
                .setRecursionDesired(true)
                .setRecursionAvailable(true)
                .setZ(false)
                .setAnswerAuthenticated(false)
                .setNonAuthenticatedData(false)
                .setReplyCode(0)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 1)
                .setAuthorityRRCount((short) 0)
                .setAdditionalRRCount((short) 0)
                .setQueries(query)
                .setAnswers(answer)
                .build();

        DNSPacketParser parser = new DNSPacketParser(googleResponseWithNegativeAddress());

        assertThat(parser.getDNSPacket(), is(equalTo(packet)));
    }

    @Test
    public void parseWhenResponseContainsCName() {
        DNSPacket.DNSQuery question = new DNSPacket.DNSQuery("cname.example.com", (short) 1, (short) 1);

        DNSPacket.DNSQuery query1 = new DNSPacket.DNSQuery("cname.example.com", (short) 5, (short) 1);
        DNSPacket.DNSAnswer answer1 = new DNSPacket.DNSAnswer(query1, 115, (short) 6, "www.example.com");

        DNSPacket.DNSQuery query2 = new DNSPacket.DNSQuery("www.example.com", (short) 1, (short) 1);
        DNSPacket.DNSAnswer answer2 = new DNSPacket.DNSAnswer(query2, 115, (short) 4, "127.0.0.2");

        DNSPacket packet = new DNSPacketBuilder()
                .setId(SamplePackets.DEFAULT_ID)
                .setResponse(true)
                .setOpCode(0)
                .setAuthoritative(false)
                .setTruncated(false)
                .setRecursionDesired(true)
                .setRecursionAvailable(true)
                .setZ(false)
                .setAnswerAuthenticated(false)
                .setNonAuthenticatedData(false)
                .setReplyCode(0)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 2)
                .setAuthorityRRCount((short) 0)
                .setAdditionalRRCount((short) 0)
                .setQueries(question)
                .setAnswers(answer1, answer2)
                .build();

        DNSPacketParser parser = new DNSPacketParser(SamplePackets.RESPONSE_CNAME_EXAMPLE_COM);

        assertThat(parser.getDNSPacket(), is(equalTo(packet)));
    }

    @Test
    public void parseRootNSResponse() {
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("", DNSPacket.TYPE_NS, (short) 1);

        DNSPacket.DNSAnswer answer1 = new DNSPacket.DNSAnswer(query, 115, (short) 20, "a.root-servers.net");

        DNSPacket.DNSAnswer answer2 = new DNSPacket.DNSAnswer(query, 115, (short) 4, "b.root-servers.net");

        DNSPacket packet = new DNSPacketBuilder()
                .setId(SamplePackets.DEFAULT_ID)
                .setResponse(true)
                .setOpCode(0)
                .setAuthoritative(false)
                .setTruncated(false)
                .setRecursionDesired(false)
                .setRecursionAvailable(true)
                .setZ(false)
                .setAnswerAuthenticated(false)
                .setNonAuthenticatedData(false)
                .setReplyCode(0)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 2)
                .setAuthorityRRCount((short) 0)
                .setAdditionalRRCount((short) 0)
                .setQueries(query)
                .setAnswers(answer1, answer2)
                .build();

        DNSPacketParser parser = new DNSPacketParser(SamplePackets.RESPONSE_ROOT_NS);

        assertThat(parser.getDNSPacket(), is(equalTo(packet)));
    }

    @Test
    public void parseResponseWithAuthoritativeAndAdditionalSectionWithIPv6Addresses() {
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("com", DNSPacket.TYPE_NS, (short) 1);

        DNSPacket.DNSAnswer authoritativeNameServer1 = new DNSPacket.DNSAnswer(query, 115, (short) 20, "a.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer2 = new DNSPacket.DNSAnswer(query, 115, (short) 4, "b.gtld-servers.net");
        DNSPacket.DNSQuery aNSQuery = new DNSPacket.DNSQuery("a.gtld-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSQuery bNSQuery = new DNSPacket.DNSQuery("b.gtld-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSAnswer aNSAnswer = new DNSPacket.DNSAnswer(aNSQuery, 115, (short) 4, "127.0.0.1");
        DNSPacket.DNSAnswer bNSAnswer = new DNSPacket.DNSAnswer(bNSQuery, 115, (short) 4, "127.0.0.1");

        DNSPacket packet = new DNSPacketBuilder()
                .setId(SamplePackets.DEFAULT_ID)
                .setResponse(true)
                .setOpCode(0)
                .setAuthoritative(false)
                .setTruncated(false)
                .setRecursionDesired(false)
                .setRecursionAvailable(false)
                .setZ(false)
                .setAnswerAuthenticated(false)
                .setNonAuthenticatedData(false)
                .setReplyCode(0)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 0)
                .setAuthorityRRCount((short) 2)
                .setAdditionalRRCount((short) 4)
                .setQueries(query)
                .setAnswers()
                .setAuthoritativeNameServers(authoritativeNameServer1, authoritativeNameServer2)
                .setAdditionalAnswers(aNSAnswer, bNSAnswer)
                .build();

        DNSPacketParser parser = new DNSPacketParser(SamplePackets.RESPONSE_COM_NS);

        assertThat(parser.getDNSPacket(), is(equalTo(packet)));

    }

    @Test
    public void parseResponseWithOnlyAuthoritativeNameServers() {
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("example.com", DNSPacket.TYPE_NS, (short) 1);

        DNSPacket.DNSAnswer authoritativeNameServer1 = new DNSPacket.DNSAnswer(query, 115, (short) 20, "a.iana-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer2 = new DNSPacket.DNSAnswer(query, 115, (short) 4, "b.iana-servers.net");

        DNSPacket packet = new DNSPacketBuilder()
                .setId(SamplePackets.DEFAULT_ID)
                .setResponse(true)
                .setOpCode(0)
                .setAuthoritative(false)
                .setTruncated(false)
                .setRecursionDesired(false)
                .setRecursionAvailable(false)
                .setZ(false)
                .setAnswerAuthenticated(false)
                .setNonAuthenticatedData(false)
                .setReplyCode(0)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 0)
                .setAuthorityRRCount((short) 2)
                .setAdditionalRRCount((short) 0)
                .setQueries(query)
                .setAnswers()
                .setAuthoritativeNameServers(authoritativeNameServer1, authoritativeNameServer2)
                .setAdditionalAnswers()
                .build();

        DNSPacketParser parser = new DNSPacketParser(SamplePackets.RESPONSE_EXAMPLE_NS);

        assertThat(parser.getDNSPacket(), is(equalTo(packet)));
    }

    @Test
    public void parseResponseWithSOA() {
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("www.example.com", DNSPacket.TYPE_NS, (short) 1);

        DNSPacket packet = new DNSPacketBuilder()
                .setId(SamplePackets.DEFAULT_ID)
                .setResponse(true)
                .setOpCode(0)
                .setAuthoritative(true)
                .setTruncated(false)
                .setRecursionDesired(false)
                .setRecursionAvailable(false)
                .setZ(false)
                .setAnswerAuthenticated(false)
                .setNonAuthenticatedData(false)
                .setReplyCode(0)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 0)
                .setAuthorityRRCount((short) 1)
                .setAdditionalRRCount((short) 0)
                .setQueries(query)
                .setAnswers()
                .setAuthoritativeNameServers()
                .setAdditionalAnswers()
                .build();

        DNSPacketParser parser = new DNSPacketParser(SamplePackets.RESPONSE_WWW_EXAMPLE_NS);

        assertThat(parser.getDNSPacket(), is(equalTo(packet)));
    }

    @Test
    public void parse_RESPONSE_EXAMPLE_NS_IP_ADDRESS() {
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("a.iana-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSQuery queryNS = new DNSPacket.DNSQuery("iana-servers.net", DNSPacket.TYPE_NS, DNSPacket.CLASS_1);

        DNSPacket.DNSAnswer authoritativeNameServer1 = new DNSPacket.DNSAnswer(queryNS, 115, (short) 14, "ns.icann.org");
        DNSPacket.DNSAnswer authoritativeNameServer2 = new DNSPacket.DNSAnswer(queryNS, 115, (short) 2, "a.iana-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer3 = new DNSPacket.DNSAnswer(queryNS, 115, (short) 4, "b.iana-servers.net");

        DNSPacket.DNSQuery aNSQuery = new DNSPacket.DNSQuery("a.iana-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSQuery bNSQuery = new DNSPacket.DNSQuery("b.iana-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSAnswer aNSAnswer = new DNSPacket.DNSAnswer(aNSQuery, 115, (short) 4, "127.0.0.1");
        DNSPacket.DNSAnswer bNSAnswer = new DNSPacket.DNSAnswer(bNSQuery, 115, (short) 4, "127.0.0.1");

        DNSPacket packet = new DNSPacketBuilder()
                .setId(SamplePackets.DEFAULT_ID)
                .setResponse(true)
                .setOpCode(0)
                .setAuthoritative(false)
                .setTruncated(false)
                .setRecursionDesired(false)
                .setRecursionAvailable(false)
                .setZ(false)
                .setAnswerAuthenticated(false)
                .setNonAuthenticatedData(false)
                .setReplyCode(0)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 0)
                .setAuthorityRRCount((short) 3)
                .setAdditionalRRCount((short) 4)
                .setQueries(query)
                .setAnswers()
                .setAuthoritativeNameServers(authoritativeNameServer1, authoritativeNameServer2, authoritativeNameServer3)
                .setAdditionalAnswers(aNSAnswer, bNSAnswer)
                .build();

        DNSPacketParser parser = new DNSPacketParser(SamplePackets.RESPONSE_EXAMPLE_NS_IP_ADDRESS);

        assertThat(parser.getDNSPacket(), is(equalTo(packet)));
    }

    @Test
    public void parseResponseWhenOffsetIsGreaterThan127() {
        // java automatically add negative sign to integer when casting from a negative byte.
        // but we do not want that behavior as offset is unsigned not signed.

        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("com", DNSPacket.TYPE_NS, (short) 1);

        DNSPacket.DNSAnswer authoritativeNameServer0 = new DNSPacket.DNSAnswer(query, 172800, (short) 20, "e.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer1 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "b.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer2 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "j.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer3 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "m.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer4 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "i.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer5 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "f.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer6 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "a.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer7 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "g.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer8 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "h.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer9 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "l.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer10 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "k.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer11 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "c.gtld-servers.net");
        DNSPacket.DNSAnswer authoritativeNameServer12 = new DNSPacket.DNSAnswer(query, 172800, (short) 4, "d.gtld-servers.net");

        DNSPacket.DNSQuery eNSQuery = new DNSPacket.DNSQuery("e.gtld-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSQuery bNSQuery = new DNSPacket.DNSQuery("b.gtld-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSQuery jNSQuery = new DNSPacket.DNSQuery("j.gtld-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSQuery mNSQuery = new DNSPacket.DNSQuery("m.gtld-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSQuery iNSQuery = new DNSPacket.DNSQuery("i.gtld-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSQuery fNSQuery = new DNSPacket.DNSQuery("f.gtld-servers.net", DNSPacket.TYPE_A, DNSPacket.CLASS_1);

        DNSPacket.DNSAnswer eNSAnswer = new DNSPacket.DNSAnswer(eNSQuery, 172800, (short) 4, "192.12.94.30");
        DNSPacket.DNSAnswer bNSAnswer = new DNSPacket.DNSAnswer(bNSQuery, 172800, (short) 4, "192.33.14.30");
        DNSPacket.DNSAnswer jNSAnswer = new DNSPacket.DNSAnswer(jNSQuery, 172800, (short) 4, "192.48.79.30");
        DNSPacket.DNSAnswer mNSAnswer = new DNSPacket.DNSAnswer(mNSQuery, 172800, (short) 4, "192.55.83.30");
        DNSPacket.DNSAnswer iNSAnswer = new DNSPacket.DNSAnswer(iNSQuery, 172800, (short) 4, "192.43.172.30");
        DNSPacket.DNSAnswer fNSAnswer = new DNSPacket.DNSAnswer(fNSQuery, 172800, (short) 4, "192.35.51.30");

        DNSPacket packet = new DNSPacketBuilder()
                .setId(SamplePackets.DEFAULT_ID)
                .setResponse(true)
                .setOpCode(0)
                .setAuthoritative(false)
                .setTruncated(true)
                .setRecursionDesired(false)
                .setRecursionAvailable(false)
                .setZ(false)
                .setAnswerAuthenticated(false)
                .setNonAuthenticatedData(false)
                .setReplyCode(0)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 0)
                .setAuthorityRRCount((short) 13)
                .setAdditionalRRCount((short) 12)
                .setQueries(query)
                .setAnswers()
                .setAuthoritativeNameServers(
                        authoritativeNameServer0,
                        authoritativeNameServer1,
                        authoritativeNameServer2,
                        authoritativeNameServer3,
                        authoritativeNameServer4,
                        authoritativeNameServer5,
                        authoritativeNameServer6,
                        authoritativeNameServer7,
                        authoritativeNameServer8,
                        authoritativeNameServer9,
                        authoritativeNameServer10,
                        authoritativeNameServer11,
                        authoritativeNameServer12
                )
                .setAdditionalAnswers(
                        eNSAnswer,
                        bNSAnswer,
                        jNSAnswer,
                        mNSAnswer,
                        iNSAnswer,
                        fNSAnswer
                )
                .build();
        DNSPacketParser parser = new DNSPacketParser(responseWithUnsignedByteAsOffset());

        assertThat(parser.getDNSPacket(), is(equalTo(packet)));

    }

    private byte[] googleResponseWithNegativeAddress() {
        return new byte[]{
                // header
                (byte) 0xac, (byte) 0xac, // id
                (byte) 0x81, // QR = 1, OP Code = 0000, AA = 0, TC = 0, RD = 1
                (byte) 0x80, // RA = 1, Z = 0, 0, 0, RCode = 0000
                0x00, 0x01, // QDCount
                0x00, 0x01, // ANCount
                0x00, 0x00, // NSCount
                0x00, 0x00, // ARCount
                // body
                //      query
                0x03, 0x77, 0x77, 0x77, // 3 www
                0x06, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, // 6 google
                0x03, 0x63, 0x6f, 0x6d, // 3 com
                0x00, // null
                0x00, 0x01, // QType
                0x00, 0x01, // QClass
                //      answer
                (byte) 0xc0, 0x0c, // Name, used pointer offset = 0x0c
                0x00, 0x01, // Type
                0x00, 0x01, // Class
                0x00, 0x00, 0x00, 0x73, // TTL
                0x00, 0x04, // RDLength
                (byte) 0xac, (byte) 0xd9, (byte) 0xa0, (byte) 0xc4 // RData
        };
    }

    private byte[] responseWithUnsignedByteAsOffset() {
        return new byte[]{
                (byte) 0x00, 0x01,
                (byte) 0x82,
                0x00,
                0x00, 0x01,
                0x00, 0x00,
                0x00, 0x0d,
                0x00, 0x0c,
                0x03, 0x63, 0x6f, 0x6d,
                0x00,
                0x00, 0x02,
                0x00, 0x01,

                // query
                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x14,
                0x01, 0x65,
                0x0c, 0x67, 0x74, 0x6c, 0x64, 0x2d, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x73,
                0x03, 0x6e, 0x65, 0x74,
                0x00,

                // Authoritative NS Servers
                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x62, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x6a, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x6d, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x69, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x66, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x61, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x67, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x68, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x6c, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x6b, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x63, (byte) 0xc0, 0x23,

                (byte) 0xc0, 0x0c,
                0x00, 0x02,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                0x01, 0x64, (byte) 0xc0, 0x23,

                // Additional Section
                (byte) 0xc0, 0x21,
                0x00, 0x01,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                (byte) 0xc0, 0x0c, 0x5e, 0x1e,

                (byte) 0xc0, 0x21,
                0x00, 0x1c,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x10,
                0x20, 0x01, 0x05, 0x02, 0x1c, (byte) 0xa1, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30,

                (byte) 0xc0, 0x41,
                0x00, 0x01,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                (byte) 0xc0, 0x21, 0x0e, 0x1e,

                (byte) 0xc0, 0x41,
                0x00, 0x1c,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x10,
                0x20, 0x01, 0x05, 0x03, 0x23, 0x1d, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x30,

                (byte) 0xc0, 0x51,
                0x00, 0x01,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                (byte) 0xc0, 0x30, 0x4f, 0x1e,

                (byte) 0xc0, 0x51,
                0x00, 0x1c,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x10,
                0x20, 0x01, 0x05, 0x02, 0x70, (byte) 0x94, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30,

                (byte) 0xc0, 0x61,
                0x00, 0x01,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                (byte) 0xc0, 0x37, 0x53, 0x1e,

                (byte) 0xc0, 0x61,
                0x00, 0x1c,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x10,
                0x20, 0x01, 0x05, 0x01, (byte) 0xb1, (byte) 0xf9, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30,

                (byte) 0xc0, 0x71,
                0x00, 0x01,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                (byte) 0xc0, 0x2b, (byte) 0xac, 0x1e,

                (byte) 0xc0, 0x71,
                0x00, 0x1c,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x10,
                0x20, 0x01, 0x05, 0x03, 0x39, (byte) 0xc1, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30,

                (byte) 0xc0, (byte) 0x81,
                0x00, 0x01,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x04,
                (byte) 0xc0, 0x23, 0x33, 0x1e,

                (byte) 0xc0, (byte) 0x81,
                0x00, 0x1c,
                0x00, 0x01,
                0x00, 0x02, (byte) 0xa3, 0x00,
                0x00, 0x10,
                0x20, 0x01, 0x05, 0x03, (byte) 0xd4, 0x14, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30,
        };
    }
}