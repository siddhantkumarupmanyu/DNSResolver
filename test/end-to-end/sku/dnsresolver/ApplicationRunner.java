package sku.dnsresolver;

public class ApplicationRunner {

    private ApplicationDriver driver;

    public void resolve(String domainName, FakeDnsServer dnsServer) {
        startApplication(domainName, dnsServer.ipAddress(), dnsServer.port());
    }

    private void startApplication(String domainName, String dnsServerIp, String port) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(domainName, dnsServerIp, port);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        driver = new ApplicationDriver(1000);
    }

    public void hasReceivedResponseWith(String ipAddress) {
        driver.hasLabelWithString(ipAddress);
    }
}
