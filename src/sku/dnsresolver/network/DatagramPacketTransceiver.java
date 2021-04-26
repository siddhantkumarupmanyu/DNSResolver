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
        } catch (SocketException e) {
            e.printStackTrace();
        }

        DatagramPacket datagramPacket = new DatagramPacket(packetInBytes, packetInBytes.length, socketAddress);
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] receivePacket() {
        byte[] receiveBuffer = new byte[UDP_MAX_BYTES];
        DatagramPacket packet = new DatagramPacket(receiveBuffer, UDP_MAX_BYTES);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet.getData();
    }

    private void createSocket() throws SocketException {
        socket = new DatagramSocket();
    }
}
