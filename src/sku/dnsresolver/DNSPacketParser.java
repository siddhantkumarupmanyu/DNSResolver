package sku.dnsresolver;

import org.apache.commons.lang.ArrayUtils;

public class DNSPacketParser {
    private static final int HEADER_SIZE = 12;


    private final PacketTransceiver transceiver;
    private final DNSPacketBuilder builder;

    private byte[] buffer;
    private int currentBufferIndex = 0;

    public DNSPacketParser(PacketTransceiver transceiver) {
        this.transceiver = transceiver;
        this.builder = new DNSPacketBuilder();
    }

    public DNSPacket getDNSPacket() {
        parse();
        return builder.build();
    }

    private void parse() {
        parseHeader();
        parseBody();
    }

    private void parseHeader() {
        addBytesFromTransceiver(HEADER_SIZE);

        builder.setId(getNextShortFromBuffer());
        parse_QR_OPCode_AA_TC_RD();
        parse_RA_Z_RCode();
        builder.setQuestionCount(getNextShortFromBuffer());
        builder.setAnswerRRCount(getNextShortFromBuffer());
        builder.setAuthorityRRCount(getNextShortFromBuffer());
        builder.setAdditionalRRCount(getNextShortFromBuffer());
    }

    private void parseBody() {
        builder.setQueries(parseQuery());
        builder.setAnswers(parseAnswer());
    }

    private void parse_QR_OPCode_AA_TC_RD() {
        final byte value = nextByteFromBuffer();
        boolean response = booleanFromInt((value >>> 7) & 0x01); // 1 bit
        int opCode = (value >>> 3) & 0b0000_1111; // 4 bit
        boolean authoritative = booleanFromInt((value >> 2) & 0x01); // 1 bit
        boolean truncated = booleanFromInt((value >> 1) & 0x01); // 1 bit
        boolean recursionDesired = booleanFromInt(value & 0x01); // 1 bit

        builder.setResponse(response);
        builder.setOpCode(opCode);
        builder.setAuthoritative(authoritative);
        builder.setTruncated(truncated);
        builder.setRecursionDesired(recursionDesired);
    }

    private void parse_RA_Z_RCode() {
        final byte value = nextByteFromBuffer();
        boolean recursionAvailable = booleanFromInt((value >>> 7) & 0x01); // 1 bit
        boolean z = booleanFromInt((value >>> 6) & 0x01); // 1 bit
        boolean answerAuthenticated = booleanFromInt((value >>> 5) & 0x01); // 1 bit
        boolean nonAuthenticatedData = booleanFromInt((value >>> 4) & 0x01); // 1 bit
        int replyCode = value & 0b0000_1111; // 4 bit

        builder.setRecursionAvailable(recursionAvailable);
        builder.setZ(z);
        builder.setAnswerAuthenticated(answerAuthenticated);
        builder.setNonAuthenticatedData(nonAuthenticatedData);
        builder.setReplyCode(replyCode);
    }

    private DNSPacket.DNSQuery parseQuery() {
        String query = parseQueryLabels();

        addBytesFromTransceiver(4);
        short qType = getNextShortFromBuffer();
        short qClass = getNextShortFromBuffer();

        return new DNSPacket.DNSQuery(query, qType, qClass);
    }

    private DNSPacket.DNSAnswer parseAnswer() {
        addBytesFromTransceiver(2);
        DNSPacket.DNSQuery query;

        if (queryIsAPointer(nextByteFromBuffer())) {
            int offset = nextByteFromBuffer();
            int oldIndex = this.currentBufferIndex;
            this.currentBufferIndex = offset;
            query = parseQuery();
            this.currentBufferIndex = oldIndex;
            addBytesFromTransceiver(4);
            nextByteFromBuffer();
            nextByteFromBuffer();
            nextByteFromBuffer();
            nextByteFromBuffer();
        } else {
            query = parseQuery();
        }

        addBytesFromTransceiver(6);
        int ttl = getNextIntFromBuffer();
        short dataLength = getNextShortFromBuffer();

        addBytesFromTransceiver(dataLength);
        int address = getNextIntFromBuffer();
        return new DNSPacket.DNSAnswer(query, ttl, dataLength, address);
    }

    private boolean queryIsAPointer(byte currentByte) {
        return currentByte == ((byte) 0xc0);
    }

    private String parseQueryLabels() {
        StringBuilder stringBuilder = new StringBuilder();

        byte currentCharacter = nextByteFromBuffer();

        while (currentCharacter != 0x00) {
            int labelCount = currentCharacter;
            for (int i = 0; i < labelCount; i++) {
                stringBuilder.append((char) nextByteFromBuffer());
            }

            currentCharacter = nextByteFromBuffer();

            if (currentCharacter == 0x00) {
                break;
            }

            stringBuilder.append(".");
        }

        return stringBuilder.toString();
    }

    private short getNextShortFromBuffer() {
        int upperByte = nextByteFromBuffer() & 0x000000ff;
        int lowerByte = nextByteFromBuffer() & 0x000000ff;
        return (short) ((upperByte << 8) | (lowerByte));
    }

    private int getNextIntFromBuffer() {
        int upperByte = nextByteFromBuffer() & 0x000000ff;
        int upperMiddle = nextByteFromBuffer() & 0x000000ff;
        int lowerMiddle = nextByteFromBuffer() & 0x000000ff;
        int lowerByte = nextByteFromBuffer() & 0x000000ff;
        return ((upperByte << 24) | (upperMiddle << 16) | (lowerMiddle << 8) | (lowerByte));
    }

    private byte nextByteFromBuffer() {
        if (currentBufferIndex == buffer.length) {
            addBytesFromTransceiver(1);
        }
        return buffer[currentBufferIndex++];
    }

    private boolean booleanFromInt(int value) {
        return value == 0 ? false : true;
    }

    private void addBytesFromTransceiver(int numberOfBytes) {
        this.buffer = ArrayUtils.addAll(this.buffer, transceiver.readNextBytes(numberOfBytes));
    }
}

// No need for this comment,
// But just in case we need it
// https://stackoverflow.com/questions/29945627/java-8-lambda-void-argument
