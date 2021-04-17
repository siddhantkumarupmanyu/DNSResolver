import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class FakeServerThread extends Thread {

    private final RequestListener requestListener;
    private final DatagramSocket socket;

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

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    public void stopThread() {
        active = false;
    }

    private int getPort() {
        return Integer.parseInt(FakeDnsServer.FAKE_DNS_PORT);
    }

    private InetAddress getIpAddress() throws UnknownHostException {
        return InetAddress.getByName(FakeDnsServer.FAKE_DNS_IP_ADDRESS);
    }
}
