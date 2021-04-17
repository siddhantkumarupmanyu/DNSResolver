import sku.dnsresolver.Main;

public class ApplicationRunner {

    private ApplicationDriver driver;

    public void resolve(String domainName, FakeDnsServer dnsServer) {
        startApplication(domainName, dnsServer.ipAddress(), dnsServer.port());
    }

    private void startApplication(String domainName, String dnsServerIp, String port) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                Main.main(domainName, dnsServerIp, port);
            }
        };
        thread.setDaemon(true);
        thread.start();

        driver = new ApplicationDriver(1000);
    }

    public void hasReceivedResponse(String ipAddress) {

    }
}
