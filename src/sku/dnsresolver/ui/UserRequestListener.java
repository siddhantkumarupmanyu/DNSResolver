package sku.dnsresolver.ui;

import java.util.EventListener;

public interface UserRequestListener extends EventListener {
    void resolve(String domainName, String serverIp, String serverPort);
}
