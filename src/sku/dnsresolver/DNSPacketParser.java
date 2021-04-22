package sku.dnsresolver;

import org.apache.commons.lang.BooleanUtils;

public class DNSPacketParser {


    private final PacketTransceiver transceiver;
    private final byte[] headerBytes;
    private final DNSExchange.DNSExchangeBuilder builder;

    private int currentHeaderByteIndex = 0;

    public DNSPacketParser(PacketTransceiver transceiver) {
        this.transceiver = transceiver;
        this.headerBytes = transceiver.readHeaderBytes();
        this.builder = new DNSExchange.DNSExchangeBuilder();
    }

    private void parse() {
        parseHeader();
        parseBody();
    }

    private void parseBody() {
        String query = getQuery();
//        short qType = getQType();
//        short qClass = getQClass();

        builder.setQuery(query);
    }

    // TODO: refactor this
    private String getQuery() {
        StringBuilder stringBuilder = new StringBuilder();

        byte currentCharacter = nextByteBody();

        while (currentCharacter != 0x00) {
            int labelCount = currentCharacter;
            for (int i = 0; i < labelCount; i++) {
                stringBuilder.append((char) nextByteBody());
            }
            currentCharacter = nextByteBody();

            if (currentCharacter == 0x00) {
                break;
            }

            stringBuilder.append(".");
        }

        return stringBuilder.toString();
    }

    private byte nextByteBody() {
        return transceiver.readNextByte();
    }

    private short getQClass() {
        return 0;
    }

    private short getQType() {
        return 0;
    }

    private void parseHeader() {
        short id = getId();
        int[] qrArray = get_QR_OPCode_AA_TC_RD();
        boolean recursion = BooleanUtils.toBoolean(qrArray[4]);
        int[] raArray = get_RA_Z_RCode();
        short qdCount = getQDCount();
        short anCount = getANCount();
        short nsCount = getASCount();
        short arCount = getARCount();

        builder.setId(id);
        builder.setRecursion(recursion);
    }

    private short getARCount() {
        // TODO: implement this
        nextByteHeader();
        nextByteHeader();
        return 0;
    }

    private short getASCount() {
        // TODO: implement this
        nextByteHeader();
        nextByteHeader();
        return 0;
    }

    private short getANCount() {
        // TODO: implement this
        nextByteHeader();
        nextByteHeader();
        return 0;
    }

    private short getQDCount() {
        // TODO: implement this
        nextByteHeader();
        nextByteHeader();
        return 0;
    }

    private int[] get_RA_Z_RCode() {
        // TODO: implement this
        nextByteHeader();
        return new int[0];
    }

    private int[] get_QR_OPCode_AA_TC_RD() {
        final byte value = nextByteHeader();
        int qr = value >>> 8;
        int opCode = (value & 0b01111000) >>> 3;
        int aa = (value & 0b00000100) >>> 2;
        int tc = (value & 0b00000010) >>> 1;
        int rd = (value & 0b0000001);
        return new int[]{qr, opCode, aa, tc, rd};
    }

    private short getId() {
        byte upperByte = nextByteHeader();
        byte lowerByte = nextByteHeader();
        return (short) ((upperByte << 8) | (lowerByte));
    }

    private byte nextByteHeader() {
        return headerBytes[currentHeaderByteIndex++];
    }

    public DNSExchange getDNSExchange() {
        parse();
        return builder.build2();
    }
}
