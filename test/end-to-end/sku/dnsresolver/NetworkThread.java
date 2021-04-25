package sku.dnsresolver;

import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static sku.dnsresolver.DatagramPacketTransceiver.UDP_MAX_BYTES;

public class NetworkThread extends Thread {

    public static final int QUERY_LABELS_STARTING_INDEX = 12;
    private final DatagramSocket socket;

    private final ExecutorService sender = Executors.newSingleThreadExecutor();
    private final DNSMessageListener messageListener;

    private boolean active = true;

    public NetworkThread(DNSMessageListener messageListener) throws SocketException {
        this.socket = new DatagramSocket();
        this.messageListener = messageListener;
    }

    public NetworkThread(DNSMessageListener messageListener, DNSSocketAddress socketAddress) throws Exception {
        this.messageListener = messageListener;
        this.socket = new DatagramSocket(socketAddress.inetSocketAddress());
    }

    @Override
    public void run() {
        while (active) {
            try {
                DatagramPacket packet = receiveDatagramPacket();
                DNSMessage message = createDNSMessageFromReceivedPacket(packet);
                notifyMessageListener(message);
            } catch (SocketException e) {
                // not implemented
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendDNSPacketWithAnswer(DNSPacket packet, byte[] ipAddress, final DNSSocketAddress dnsSocketAddress) {
        sender.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] queryPacket = new DNSPacketGenerator(packet).getBytes();
                    byte[] answer = {
                            (byte) 0xc0, 0x0c, // Name, used pointer offset = 0x0c
                            0x00, 0x01, // Type
                            0x00, 0x01, // Class
                            0x00, 0x00, 0x00, 0x73, // TTL
                            0x00, 0x04, // RDLength
                            ipAddress[0],
                            ipAddress[1],
                            ipAddress[2],
                            ipAddress[3]
                    };
                    byte[] answerPacket = ArrayUtils.addAll(queryPacket, answer);
                    answerPacket[2] = (byte) 0x81; // set as response
                    answerPacket[3] = (byte) 0x80; // set recursion available to 1
                    answerPacket[7] = 0x01; // set answerRR to 1
                    sendBytes(answerPacket, dnsSocketAddress.inetSocketAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopThread() {
        active = false;
        socket.close();
        sender.shutdown();
    }

    private DatagramPacket receiveDatagramPacket() throws IOException {
        byte[] receiveBuffer = new byte[UDP_MAX_BYTES];
        DatagramPacket packet = new DatagramPacket(receiveBuffer, UDP_MAX_BYTES);
        socket.receive(packet);
        return packet;
    }

    private DNSMessage createDNSMessageFromReceivedPacket(DatagramPacket datagramPacket) {
        DNSPacket dnsPacket = createDNSPacketFrom(datagramPacket);
        DNSSocketAddress socketAddress = DNSSocketAddress.from((InetSocketAddress) datagramPacket.getSocketAddress());
        return new DNSMessage(socketAddress, dnsPacket);
    }

    private DNSPacket createDNSPacketFrom(DatagramPacket datagramPacket) {
        String query = parseQueryLabels(datagramPacket.getData());
        return new DNSQueryBuilder()
                .setId(FakeDnsServer.DEFAULT_ID)
                .setRecursionDesired(FakeDnsServer.DEFAULT_RECURSION)
                .setQueries(new DNSPacket.DNSQuery(query, FakeDnsServer.DEFAULT_QTYPE, FakeDnsServer.DEFAULT_QCLASS))
                .build();
    }

    private void notifyMessageListener(DNSMessage message) {
        messageListener.receivedMessage(message);
    }

    private void sendBytes(byte[] packet, InetSocketAddress socketAddress) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(packet, packet.length, socketAddress);
        socket.send(datagramPacket);
    }

    private String parseQueryLabels(byte[] queryLabels) {
        StringBuilder stringBuilder = new StringBuilder();
        int currentIndex = QUERY_LABELS_STARTING_INDEX;

        byte currentCharacter = queryLabels[currentIndex++];

        while (currentCharacter != DNSPacketGenerator.NULL_TERMINATOR) {
            int labelCount = currentCharacter;

            byte[] label = new byte[labelCount];
            for (int i = 0; i < labelCount; i++) {
                label[i] = queryLabels[currentIndex++];
            }
            stringBuilder.append(new String(label, 0, labelCount, StandardCharsets.UTF_8));

            currentCharacter = queryLabels[currentIndex++];

            if (currentCharacter != DNSPacketGenerator.NULL_TERMINATOR) {
                stringBuilder.append(".");
            }
        }

        return stringBuilder.toString();
    }
}
