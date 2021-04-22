package sku.dnsresolver;

public class DNSExchangeConverter {

    public static byte[] generateDNSPacketInBytes(DNSExchange exchange) {
        DNSPacketGenerator generator = new DNSPacketGenerator(exchange);
        return generator.getBytes();
    }

}
