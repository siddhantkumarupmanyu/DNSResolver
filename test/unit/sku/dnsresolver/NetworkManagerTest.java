package sku.dnsresolver;


import org.junit.Test;

public class NetworkManagerTest {


    // TODO
    @Test
    public void sendDNSExchange() {
        DNSExchange exchange = new DNSExchange.DNSExchangeBuilder()
                .setId((short) 1)
                .setRecursion(true)
                .setQuery("www.example.com")
                .build2();

        NetworkManager networkManager = new NetworkManager();

//        networkManager.send();
    }

}

// this is an integration test