package sku.dnsresolver;

import org.apache.commons.lang.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DNSExchangeParser {

    private static ArrayList<Byte> packet;

    public static byte[] generateDNSPacketInBytes(DNSExchange exchange) {
        packet = new ArrayList<>();

        // header
        insertId(exchange.id);
        insert_QR_OPCode_AA_TC_RD(exchange.recursion);
        insert_RA_Z_RCode();
        insertQDCount();
        insertANCount();
        insertNSCount();
        insertARCount();

        //body
        insertQuery(exchange.question);
        insertQType();
        insertQClass();

        return toPrimitiveArray();
    }

    private static void insertId(short id) {
        byte upperByte = (byte) (id >>> 8);
        byte lowerByte = (byte) id;
        packet.add(upperByte);
        packet.add(lowerByte);
    }

    private static void insertQClass() {
        packet.add((byte) 0x00);
        packet.add((byte) 0x01);
    }

    private static void insertQType() {
        packet.add((byte) 0x00);
        packet.add((byte) 0x01);
    }

    private static void insertQuery(String question) {
        String[] labels = question.split("\\.");

        for (String label : labels) {
            packet.add((byte) label.length());
            for (byte b : label.getBytes(StandardCharsets.UTF_8)) {
                packet.add(b);
            }
        }

        packet.add((byte) 0x00); // termination
    }

    private static void insertARCount() {
        packet.add((byte) 0x00);
        packet.add((byte) 0x00);
    }

    private static void insertNSCount() {
        packet.add((byte) 0x00);
        packet.add((byte) 0x00);
    }

    private static void insertANCount() {
        packet.add((byte) 0x00);
        packet.add((byte) 0x00);
    }

    private static void insertQDCount() {
        packet.add((byte) 0x00);
        packet.add((byte) 0x01);
    }

    private static void insert_RA_Z_RCode() {
        packet.add((byte) 0x00);
    }

    private static void insert_QR_OPCode_AA_TC_RD(boolean recursion) {
        byte defaultCodes = 0x00;
        if (recursion) {
            defaultCodes = (byte) (defaultCodes | 0x1);
        }
        packet.add(defaultCodes);
    }

    private static byte[] toPrimitiveArray() {
        return ArrayUtils.toPrimitive(packet.toArray(new Byte[0]));
    }

}
