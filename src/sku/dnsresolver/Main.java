package sku.dnsresolver;

import sku.dnsresolver.network.DNSSocketAddress;
import sku.dnsresolver.network.DatagramFactory;
import sku.dnsresolver.network.NetworkExecutor;
import sku.dnsresolver.network.SingleThreadExecutor;
import sku.dnsresolver.ui.MainWindow;
import sku.dnsresolver.ui.UserRequestListener;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main implements UserRequestListener {

    private MainWindow ui;
    private final NetworkExecutor networkExecutor;

    public Main() throws Exception {
        startUserInterface();


        DatagramFactory factory = new DatagramFactory();
        factory.setTimeout(5000);

        this.networkExecutor = new SingleThreadExecutor(factory);
        DNSResolver dnsResolver = new DNSResolver(this.networkExecutor, new SwingThreadUiListener(ui));

        this.networkExecutor.addListener(dnsResolver);

        ui.addUserRequestListener(dnsResolver);
        shutdownNetworkManagerWhenUICloses();
    }

    public static void main(String... args) {
        try {
            Main main = new Main();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }

    // no need; if need to do custom query use this
    public void resolve(String domainName, String serverIp, String serverPort, boolean recursion) {
        DNSSocketAddress dnsSocketAddress = new DNSSocketAddress(serverIp, serverPort);

        DNSPacket packet = new DNSQueryBuilder()
                .setId((short) 1)
                .setRecursionDesired(false)
//                .setQueries(new DNSPacket.DNSQuery("a.iana-servers.net", DNSPacket.TYPE_A, (short) 1))
                .setQueries(new DNSPacket.DNSQuery("www.example.com", DNSPacket.TYPE_NS, (short) 1))
                .build();

        this.networkExecutor.query(dnsSocketAddress, packet);
    }

    private void shutdownNetworkManagerWhenUICloses() {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                networkExecutor.shutdown();
            }
        });
    }
}
