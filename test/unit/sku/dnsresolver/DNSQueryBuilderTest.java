package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class DNSQueryBuilderTest {


    @Test
    public void buildADNSQueryPacket() {
        short id = 1;
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("www.example.com", (short) 1, (short) 1);
        DNSQueryBuilder builder = new DNSQueryBuilder()
                .setId(id)
                .setRecursionDesired(true)
                .setQueries(query);

        assertThat(builder.build(), is(equalTo(dnsQueryPacketWith(id))));
    }


    private DNSPacket dnsQueryPacketWith(int id) {
        DNSPacket.DNSQuery query = new DNSPacket.DNSQuery("www.example.com", (short) 1, (short) 1);
        return new DNSPacket(
                (short) id,
                false,
                0,
                false,
                false,
                true,
                false,
                false,
                false,
                false,
                0,
                (short) 1,
                (short) 0,
                (short) 0,
                (short) 0,
                new DNSPacket.DNSQuery[]{query},
                new DNSPacket.DNSAnswer[0],
                new DNSPacket.DNSAnswer[0],
                new DNSPacket.DNSAnswer[0]
        );
    }
}