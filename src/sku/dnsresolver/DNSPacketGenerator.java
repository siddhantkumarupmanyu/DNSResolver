package sku.dnsresolver;

import org.apache.commons.lang.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class DNSPacketGenerator {
    private final DNSExchange exchange;
    private final ArrayList<Byte> packet;

    public DNSPacketGenerator(DNSExchange exchange) {
        this.exchange = exchange;
        this.packet = new ArrayList<>();
    }

    public byte[] getBytes() {
        generate();
        return toPrimitiveArray();
    }

    private void generate() {
        generateHeader();
        generateBody();
    }

    private void generateHeader() {
        insertId(exchange.id);
        insert_QR_OPCode_AA_TC_RD(exchange.recursion);
        insert_RA_Z_RCode();
        insertQDCount((short) 1);
        insertANCount();
        insertNSCount();
        insertARCount();
    }

    private void generateBody() {
        insertQuery(exchange.query);
        insertQType((short) 1);
        insertQClass((short) 1);
    }

    private void insertId(short id) {
        byte upperByte = (byte) (id >>> 8);
        byte lowerByte = (byte) id;
        packet.add(upperByte);
        packet.add(lowerByte);
    }

    private void insert_QR_OPCode_AA_TC_RD(boolean recursion) {
        byte defaultCodes = 0x00;
        if (recursion) {
            defaultCodes = (byte) (defaultCodes | 0x1);
        }
        packet.add(defaultCodes);
    }

    private void insert_RA_Z_RCode() {
        packet.add((byte) 0x00);
    }

    private void insertQDCount(short queryCount) {
        packet.add((byte) 0x00);
        packet.add((byte) queryCount);
    }

    private void insertANCount() {
        packet.add((byte) 0x00);
        packet.add((byte) 0x00);
    }

    private void insertNSCount() {
        packet.add((byte) 0x00);
        packet.add((byte) 0x00);
    }

    private void insertARCount() {
        packet.add((byte) 0x00);
        packet.add((byte) 0x00);
    }

    private void insertQuery(String query) {
        String[] labels = query.split("\\.");

        for (String label : labels) {
            packet.add((byte) label.length());
            for (byte b : label.getBytes(StandardCharsets.UTF_8)) {
                packet.add(b);
            }
        }

        packet.add((byte) 0x00); // termination
    }

    private void insertQClass(short qClass) {
        packet.add((byte) 0x00);
        packet.add((byte) qClass);
    }

    private void insertQType(short type) {
        packet.add((byte) 0x00);
        packet.add((byte) type);
    }

    private byte[] toPrimitiveArray() {
        return ArrayUtils.toPrimitive(packet.toArray(new Byte[0]));
    }
}
