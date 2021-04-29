package sku.dnsresolver.ui;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import sku.dnsresolver.ApplicationDriver;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {

    private final MainWindow mainWindow = new MainWindow();
    private final ApplicationDriver driver = new ApplicationDriver(100);

    @Test
    public void makesUserRequestWhenResolveButtonIsClicked() {
        final ValueMatcherProbe<String> domainNameProbe = new ValueMatcherProbe<>(equalTo("www.example.com"), "domain name");
        final ValueMatcherProbe<String> serverIpProbe = new ValueMatcherProbe<>(equalTo("127.0.0.1"), "ip address");
        final ValueMatcherProbe<String> serverPortProbe = new ValueMatcherProbe<>(equalTo("53"), "port");

        mainWindow.addUserRequestListener(
                new UserRequestListener() {

                    @Override
                    public void resolve(String domainName, String serverIp, String port) {
                        domainNameProbe.setReceivedValue(domainName);
                        serverIpProbe.setReceivedValue(serverIp);
                        serverPortProbe.setReceivedValue(port);
                    }
                }

        );

        driver.resolveDomainName("www.example.com", "127.0.0.1", "53");
        driver.check(domainNameProbe);
        driver.check(serverIpProbe);
    }

}

// this is a integration test