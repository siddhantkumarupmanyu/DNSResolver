package sku.dnsresolver;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FakeServerThread extends Thread {

    private final RequestListener requestListener;
    private final DatagramSocket socket;
    private final ExecutorService sender = Executors.newSingleThreadExecutor();

    private SocketAddress lastSocketAddress;

    private boolean active = true;

    public FakeServerThread(RequestListener requestListener) throws IOException {
        super("Fake Server Thread");
        this.requestListener = requestListener;
        this.socket = new DatagramSocket(getPort(), getIpAddress());
    }

    @Override
    public void run() {
        while (active) {
            try {
                byte[] buffer = new byte[1024];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String request = new String(packet.getData(), 0, packet.getLength());

                requestListener.newRequest(request);

                lastSocketAddress = packet.getSocketAddress();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    public void respondWith(String ipaddress) {
        sender.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendPacket(ipaddress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopThread() {
        active = false;
        sender.shutdown();
    }

    private void sendPacket(String ipAddress) throws IOException {
        byte[] response = ipAddress.getBytes();
        DatagramPacket packet = new DatagramPacket(response, response.length, lastSocketAddress);
        socket.send(packet);
    }

    private int getPort() {
        return Integer.parseInt(FakeDnsServer.FAKE_DNS_PORT);
    }

    private InetAddress getIpAddress() throws UnknownHostException {
        return InetAddress.getByName(FakeDnsServer.FAKE_DNS_IP_ADDRESS);
    }
}
