package sku.dnsresolver;

public class ApplicationRunner {

    private ApplicationDriver driver;

    public void resolve(String domainName, FakeDnsServer dnsServer) {
        startApplication();
        driver.resolveDomainName(domainName, dnsServer.ipAddress(), dnsServer.port(), true);
    }

    public void resolveWithoutRecursion(String domainName, FakeDnsServer dnsServer) {
        startApplication();
        driver.resolveDomainName(domainName, dnsServer.ipAddress(), dnsServer.port(), false);
    }

    private void startApplication() {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main();
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
        driver.responseTextAreaContains(ipAddress);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}
