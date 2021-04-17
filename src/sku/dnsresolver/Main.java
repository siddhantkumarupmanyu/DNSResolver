package sku.dnsresolver;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

    private MainWindow ui;

    public Main() {
    }

    public static void main(String... args) throws Exception {
        String domainName = args[0];
        String dnsServerIp = args[1];
        String dnsServerPort = args[2];

        Main main = new Main();
        main.startUserInterface();

        try {
            sendPacket(dnsServerIp, dnsServerPort);
        } catch (IOException e) {
            e.printStackTrace();
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

    private static void sendPacket(String ip, String portString) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        int port = Integer.parseInt(portString);

        byte[] buf;

        buf = "A random value".getBytes();

        InetAddress address = InetAddress.getByName(ip);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

        socket.send(packet);

    }
}
