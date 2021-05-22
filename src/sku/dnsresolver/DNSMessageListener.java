package sku.dnsresolver;

import java.util.EventListener;

public interface DNSMessageListener extends EventListener {
    void receiveMessage(DNSMessage message);
}
