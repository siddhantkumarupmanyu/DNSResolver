package sku.dnsresolver;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class PacketFormatterTest {

    @Test
    public void formatPacketToMultilineString() {
        DNSPacket packet = createDnsPacket();

        PacketFormatter formatter = new PacketFormatter();
        assertThat(formatter.format(packet), is(equalTo("test")));
    }

    private DNSPacket createDnsPacket() {
        return null;
    }

}