package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DNSPacketBuilderTest {

    @Test
    public void ableToBuildADNSPacket() {
        short id = 1;
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("www.example.com", (short) 1, (short) 1);
        DNSPacket.DNSAnswer answer = new DNSPacket.DNSAnswer(query, 256, (short) 4, "127.0.0.1");
        DNSPacket.DNSAnswer authoritativeNameServer = new DNSPacket.DNSAnswer(query, 256, (short) 4, "ns.example.com");
        DNSPacket.DNSQuery nsQuery = new DNSPacket.DNSQuery("ns.example.com", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSAnswer additional = new DNSPacket.DNSAnswer(nsQuery, 256, (short) 4, "127.0.0.2");

        DNSPacketBuilder builder = new DNSPacketBuilder()
                .setId(id)
                .setResponse(true)
                .setOpCode(1)
                .setAuthoritative(true)
                .setTruncated(true)
                .setRecursionDesired(true)
                .setRecursionAvailable(true)
                .setZ(true)
                .setAnswerAuthenticated(true)
                .setNonAuthenticatedData(true)
                .setReplyCode(1)
                .setQuestionCount((short) 1)
                .setAnswerRRCount((short) 1)
                .setAuthorityRRCount((short) 1)
                .setAdditionalRRCount((short) 1)
                .setQueries(query)
                .setAnswers(answer)
                .setAuthoritativeNameServers(authoritativeNameServer)
                .setAdditionalAnswers(additional);

        assertThat(builder.build(), is(equalTo(dnsPacketWithValuesMoreThanZeroAndId(id))));
    }

    private DNSPacket dnsPacketWithValuesMoreThanZeroAndId(int id) {
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("www.example.com", (short) 1, (short) 1);
        DNSPacket.DNSAnswer answer = new DNSPacket.DNSAnswer(query, 256, (short) 4, "127.0.0.1");
        DNSPacket.DNSAnswer authoritativeNameServer = new DNSPacket.DNSAnswer(query, 256, (short) 4, "ns.example.com");
        DNSPacket.DNSQuery nsQuery = new DNSPacket.DNSQuery("ns.example.com", DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        DNSPacket.DNSAnswer additional = new DNSPacket.DNSAnswer(nsQuery, 256, (short) 4, "127.0.0.2");

        return new DNSPacket(
                (short) id,
                true,
                1,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                1,
                (short) 1,
                (short) 1,
                (short) 1,
                (short) 1,
                new DNSPacket.DNSQuery[]{query},
                new DNSPacket.DNSAnswer[]{answer},
                new DNSPacket.DNSAnswer[]{authoritativeNameServer},
                new DNSPacket.DNSAnswer[]{additional}
        );
    }

}