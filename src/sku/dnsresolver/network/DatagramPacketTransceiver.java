package sku.dnsresolver.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;


public class DatagramPacketTransceiver implements PacketTransceiver {
    public static final int UDP_MAX_BYTES = 1400;

    private DatagramSocket socket;

    @Override
    public void sendPacket(PacketTransceiver.Packet packet) {
        try {
            createSocket();

            DatagramPacket datagramPacket = new DatagramPacket(packet.data, packet.data.length, packet.address);
            socket.send(datagramPacket);
        } catch (IOException e) {
//            e.printStackTrace();
            throw new NetworkException("Unable to Send Packet", e);
        }
    }

    @Override
    public PacketTransceiver.Packet receivePacket() {
        try {
            byte[] receiveBuffer = new byte[UDP_MAX_BYTES];
            DatagramPacket datagramPacket = new DatagramPacket(receiveBuffer, UDP_MAX_BYTES);
            socket.receive(datagramPacket);

            byte[] packetInBytes = Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength());
            return new Packet((InetSocketAddress) datagramPacket.getSocketAddress(), packetInBytes);
        } catch (IOException e) {
//            e.printStackTrace();
            throw new NetworkException("Unable to Receive Packet", e);
        } finally {
            closeSocket();
        }
    }

    private void createSocket() throws SocketException {
        socket = new DatagramSocket();
    }

    private void closeSocket() {
        socket.close();
    }
}
