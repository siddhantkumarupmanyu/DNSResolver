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
        DNSPacket.DNSAnswer aNSAnswer = new DNSPacket.DNSAnswer(aNSQuery, 115, (short) 4, "192.12.94.30");
        DNSPacket.DNSAnswer bNSAnswer = new DNSPacket.DNSAnswer(bNSQuery, 115, (short) 4, "192.33.14.30");

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

}