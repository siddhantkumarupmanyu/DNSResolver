package sku.dnsresolver;

import org.apache.commons.lang.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class DNSPacketGenerator {
    public static final int NULL_TERMINATOR = 0x00;
    private final DNSPacket packet;
    private final ArrayList<Byte> bytes;

    public DNSPacketGenerator(DNSPacket packet) {
        this.packet = packet;
        this.bytes = new ArrayList<>();
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
        insertShort(packet.id);
        insert_QR_OPCode_AA_TC_RD();
        insert_RA_Z_RCode();
        insertShort(packet.questionCount);
        insertShort(packet.answerRRCount);
        insertShort(packet.authorityRRCount);
        insertShort(packet.additionalRRCount);
    }

    private void generateBody() {
        generateQuery();
    }

    private void insert_QR_OPCode_AA_TC_RD() {
        byte response = (byte) (intFromBoolean(packet.response) << 7); // 1 bit
        byte opCode = (byte) (packet.opCode << 3); // 4 bit
        byte authoritative = (byte) (intFromBoolean(packet.authoritative) << 2); // 1 bit
        byte truncated = (byte) (intFromBoolean(packet.truncated) << 1); // 1 bit
        byte recursionDesired = (byte) (intFromBoolean(packet.recursionDesired)); // 1 bit

        byte finalByte = (byte) (response | opCode | authoritative | truncated | recursionDesired);
        bytes.add(finalByte);
    }

    private void insert_RA_Z_RCode() {
        byte recursionAvailable = (byte) (intFromBoolean(packet.response) << 7); // 1 bit
        byte z = (byte) (intFromBoolean(packet.z) << 6); // 1 bit
        byte answerAuthenticated = (byte) (intFromBoolean(packet.answerAuthenticated) << 5); // 1 bit
        byte nonAuthenticatedData = (byte) (intFromBoolean(packet.nonAuthenticatedData) << 4); // 1 bit
        byte replyCode = (byte) (packet.replyCode); // 4 bit

        byte finalByte = (byte) (recursionAvailable | z | answerAuthenticated | nonAuthenticatedData | replyCode);
        bytes.add(finalByte);
    }

    private void generateQuery() {
        insertQueryLabels();
        insertShort(packet.queries[0].qType);
        insertShort(packet.queries[0].qClass);
    }

    private void insertQueryLabels() {
        String[] labels = packet.queries[0].query.split("\\.");

        for (String label : labels) {
            bytes.add((byte) label.length());
            for (byte b : label.getBytes(StandardCharsets.UTF_8)) {
                bytes.add(b);
            }
        }

        bytes.add((byte) NULL_TERMINATOR);
    }

    private byte[] toPrimitiveArray() {
        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
    }

    private void insertShort(short number) {
        byte upperByte = (byte) (number >>> 8);
        byte lowerByte = (byte) number;
        bytes.add(upperByte);
        bytes.add(lowerByte);
    }

    private int intFromBoolean(boolean bool) {
        return bool ? 1 : 0;
    }
}
