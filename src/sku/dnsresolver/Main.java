package sku.dnsresolver;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main implements DNSMessageListener {

    public static final int DOMAIN_NAME = 0;
    public static final int DNS_SERVER_IP = 1;
    public static final int DNS_SERVER_PORT = 2;

    private MainWindow ui;
    private final NetworkManager networkManager;

    public Main(String domain, String ip, String port) throws Exception {
        startUserInterface();
        shutdownNetworkManagerWhenUICloses();

        this.networkManager = new NetworkManager(new DatagramPacketTransceiver(), this);

        DNSSocketAddress dnsSocketAddress = new DNSSocketAddress(ip, port);

        DNSPacket packet = new DNSQueryBuilder()
                .setId((short) 1)
                .setRecursionDesired(true)
                .setQueries(new DNSPacket.DNSQuery(domain, (short) 1, (short) 1))
                .build();

        this.networkManager.query(dnsSocketAddress, packet);
    }

    @Override
    public void receivedMessage(DNSMessage dnsMessage) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DNSPacket.DNSAnswer answer = dnsMessage.packet.answers[0];
                ui.setLabelText(answer.readableAddress());
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

    private void shutdownNetworkManagerWhenUICloses() {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                networkManager.shutdown();
            }
        });
    }
}
