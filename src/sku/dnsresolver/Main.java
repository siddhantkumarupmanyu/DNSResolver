package sku.dnsresolver;

import javax.swing.*;
import java.net.SocketException;

public class Main implements DNSMessageListener {

    public static final int DOMAIN_NAME = 0;
    public static final int DNS_SERVER_IP = 1;
    public static final int DNS_SERVER_PORT = 2;

    private MainWindow ui;
    private DNSThread dnsThread;

    public Main(String domain, String ip, String port) throws Exception {
        startUserInterface();
        startDnsThread();

        DNSSocketAddress dnsSocketAddress = new DNSSocketAddress(ip, port);
        this.dnsThread.sendRequest(domain, dnsSocketAddress);
    }

    private void startDnsThread() throws SocketException {
        this.dnsThread = new DNSThread(this);
        this.dnsThread.start();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main(args[DOMAIN_NAME], args[DNS_SERVER_IP], args[DNS_SERVER_PORT]);
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }

    @Override
    public void message(String dnsMessage) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ui.setLabelText(dnsMessage);
            }
        });
    }
}
