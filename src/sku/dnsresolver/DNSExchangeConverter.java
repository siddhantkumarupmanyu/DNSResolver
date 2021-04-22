package sku.dnsresolver;

public class DNSExchangeConverter {

    public static byte[] generateDNSPacketInBytes(DNSExchange exchange) {
        DNSPacketGenerator generator = new DNSPacketGenerator(exchange);
        return generator.getBytes();
    }

    public static DNSExchange generateDNSExchangeFromBytes(byte[] bytes){
        DNSPacketParser parser = new DNSPacketParser(bytes);
        return parser.getDNSExchange();
    }

}
