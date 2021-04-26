package sku.dnsresolver.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class DatagramPacketTransceiver implements PacketTransceiver {
    public static final int UDP_MAX_BYTES = 1400;

    private DatagramSocket socket;

    @Override
    public void sendPacket(byte[] packetInBytes, InetSocketAddress socketAddress) {
        try {
            createSocket();

            DatagramPacket datagramPacket = new DatagramPacket(packetInBytes, packetInBytes.length, socketAddress);
            socket.send(datagramPacket);
        } catch (IOException e) {
//            e.printStackTrace();
            throw new NetworkException("Unable to Send Packet", e);
        }
    }

    @Override
    public byte[] receivePacket() {
        try {
            byte[] receiveBuffer = new byte[UDP_MAX_BYTES];
            DatagramPacket packet = new DatagramPacket(receiveBuffer, UDP_MAX_BYTES);
            socket.receive(packet);
            return packet.getData();
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
