package sku.dnsresolver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

    public static void main(String... args) {
        String domainName = args[0];
        String dnsServerIp = args[1];
        String dnsServerPort = args[2];

        try {
            sendPacket(dnsServerIp, dnsServerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
