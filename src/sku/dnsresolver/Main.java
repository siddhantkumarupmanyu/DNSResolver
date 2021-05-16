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

public class Main implements DNSMessageListener, UserRequestListener {

    private MainWindow ui;
    private final NetworkExecutor networkExecutor;

    public Main() throws Exception {
        startUserInterface();
        shutdownNetworkManagerWhenUICloses();

        this.networkExecutor = new SingleThreadExecutor(new DatagramFactory(), this);
    }

    @Override
    public void receiveMessage(DNSMessage dnsMessage) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // TODO: replace it with something like PacketFormatter.format(packet);
                PacketFormatter formatter = new PacketFormatter(dnsMessage.packet);
                ui.appendTextToResponse(formatter.getFormattedString());
            }
        });
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
                ui.addUserRequestListener(Main.this);
            }
        });
    }

    @Override
    public void resolve(String domainName, String serverIp, String serverPort, boolean recursion) {
        DNSSocketAddress dnsSocketAddress = new DNSSocketAddress(serverIp, serverPort);

        DNSPacket packet = new DNSQueryBuilder()
                .setId((short) 1)
                .setRecursionDesired(false)
//                .setQueries(new DNSPacket.DNSQuery("a.gtld-servers.net", DNSPacket.TYPE_A, (short) 1))
                .setQueries(new DNSPacket.DNSQuery("com", DNSPacket.TYPE_NS, (short) 1))
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
