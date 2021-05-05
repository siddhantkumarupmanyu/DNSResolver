package sku.dnsresolver.network;

import sku.dnsresolver.DNSPacket;

public interface NetworkExecutor {
    void query(DNSSocketAddress serverAddress, DNSPacket query);

    void shutdown();
}
