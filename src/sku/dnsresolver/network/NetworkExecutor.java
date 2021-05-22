package sku.dnsresolver.network;

import sku.dnsresolver.DNSMessageListener;
import sku.dnsresolver.DNSPacket;

public interface NetworkExecutor {
    void query(DNSSocketAddress serverAddress, DNSPacket query);

    void addListener(DNSMessageListener listener);

    void shutdown();
}
