package sku.dnsresolver;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.SocketException;

public class Main implements DNSMessageListener {

    public static final int DOMAIN_NAME = 0;
    public static final int DNS_SERVER_IP = 1;
    public static final int DNS_SERVER_PORT = 2;

    private MainWindow ui;
    private NetworkThread networkThread;

    public Main(String domain, String ip, String port) throws Exception {
        startUserInterface();
        startNetworkThread();
        stopNetworkThreadWhenUICloses();

        DNSSocketAddress dnsSocketAddress = new DNSSocketAddress(ip, port);
        this.networkThread.sendRequest(domain, dnsSocketAddress);
    }

    @Override
    public void message(DNSMessage dnsMessage) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // TODO:
//                ui.setLabelText(dnsMessage.exchange.message);
            }
        });
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

    private void startNetworkThread() throws SocketException {
        this.networkThread = new NetworkThread(this);
        this.networkThread.start();
    }

    private void stopNetworkThreadWhenUICloses() {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                networkThread.stopThread();
            }
        });
    }
}
