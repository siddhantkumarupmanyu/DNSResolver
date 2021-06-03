package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class PacketFormatterTest {

    @Test
    public void formatPacketToMultilineString() {
        PacketFormatter formatter = new PacketFormatter(dnsPacket());
        assertThat(formatter.getFormattedString(), is(equalTo(formattedPacket())));
    }

    private DNSPacket dnsPacket() {
        DNSPacket.DNSQuery question = new DNSPacket.DNSQuery("cname.example.com", (short) 1, (short) 1);

        DNSPacket.DNSQuery query1 = new DNSPacket.DNSQuery("cname.example.com", (short) 5, (short) 1);
        DNSPacket.DNSAnswer answer1 = new DNSPacket.DNSAnswer(query1, 115, (short) 6, "www.example.com");

        DNSPacket.DNSQuery query2 = new DNSPacket.DNSQuery("www.example.com", (short) 1, (short) 1);
        DNSPacket.DNSAnswer answer2 = new DNSPacket.DNSAnswer(query2, 115, (short) 4, "127.0.0.2");

        DNSPacket.DNSQuery queryNS = new DNSPacket.DNSQuery("www.example.com", DNSPacket.TYPE_NS, DNSPacket.CLASS_1);

        DNSPacket.DNSAnswer authoritativeNS1 = new DNSPacket.DNSAnswer(queryNS, 115, (short) 13, "ns1.example.com");
        DNSPacket.DNSAnswer authoritativeNS2 = new DNSPacket.DNSAnswer(queryNS, 115, (short) 13, "ns2.example.com");

        DNSPacket.DNSQuery additionalNS1 = new DNSPacket.DNSQuery("ns1.example.com", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSQuery additionalNS2 = new DNSPacket.DNSQuery("ns2.example.com", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSAnswer additional1 = new DNSPacket.DNSAnswer(additionalNS1, 115, (short) 4, "127.0.0.1");
        DNSPacket.DNSAnswer additional2 = new DNSPacket.DNSAnswer(additionalNS2, 115, (short) 4, "127.0.0.2");

        return new DNSPacketBuilder()
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
                .setAuthorityRRCount((short) 2)
                .setAdditionalRRCount((short) 4) // additional count may differ as we are dropping ipv6 while parsing
                .setQueries(question)
                .setAnswers(answer1, answer2)
                .setAuthoritativeNameServers(authoritativeNS1, authoritativeNS2)
                .setAdditionalAnswers(additional1, additional2)
                .build();
    }

    private String formattedPacket() {
        return String.join("\n",
                "id: 1",
                "response: true",
                "opcode: 0",
                "authoritative: false",
                "truncated: false",
                "recursion desired: true",
                "recursion available: true",
                "z: false",
                "answer authenticated: false",
                "non-authenticated data: false",
                "reply code: 0",
                "question count: 1",
                "answers count: 2",
                "authority RR count: 2",
                "additional RR count: 4",
                "queries",
                "+-- query: 1",
                "    +-- label: cname.example.com",
                "    +-- type: 1",
                "    +-- class: 1",
                "answers",
                "+-- answer: 1",
                "|   +-- label: cname.example.com",
                "|   +-- type: 5",
                "|   +-- class: 1",
                "|   +-- ttl: 115",
                "|   +-- length: 6",
                "|   +-- address: www.example.com",
                "|",
                "+-- answer: 2",
                "    +-- label: www.example.com",
                "    +-- type: 1",
                "    +-- class: 1",
                "    +-- ttl: 115",
                "    +-- length: 4",
                "    +-- address: 127.0.0.2",
                "authoritative NameServers",
                "+-- nameserver: 1",
                "|   +-- label: www.example.com",
                "|   +-- type: 2",
                "|   +-- class: 1",
                "|   +-- ttl: 115",
                "|   +-- length: 13",
                "|   +-- address: ns1.example.com",
                "|",
                "+-- nameserver: 2",
                "    +-- label: www.example.com",
                "    +-- type: 2",
                "    +-- class: 1",
                "    +-- ttl: 115",
                "    +-- length: 13",
                "    +-- address: ns2.example.com",
                "additional section",
                "+-- answer: 1",
                "|   +-- label: ns1.example.com",
                "|   +-- type: 1",
                "|   +-- class: 1",
                "|   +-- ttl: 115",
                "|   +-- length: 4",
                "|   +-- address: 127.0.0.1",
                "|",
                "+-- answer: 2",
                "    +-- label: ns2.example.com",
                "    +-- type: 1",
                "    +-- class: 1",
                "    +-- ttl: 115",
                "    +-- length: 4",
                "    +-- address: 127.0.0.2"
        );
    }

}