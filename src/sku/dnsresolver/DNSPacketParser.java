package sku.dnsresolver;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * This class only supports parsing response bytes to DNSPacket.
 * <br />
 * Since, the program will never act as a server,
 * this functionality(parsing a request) is excluded.
 */
public class DNSPacketParser {
    private static final int HEADER_SIZE = 12;
    public static final int NULL_TERMINATOR = 0x00;
    public static final int POINTER_TYPE = 0xc0;

    private final DNSPacketBuilder builder;

    private final byte[] response;
    private int currentBufferIndex = 0;

    public DNSPacketParser(byte[] response) {
        this.response = response;
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
        final byte value = nextByte();
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
        final byte value = nextByte();
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

        short qType = getNextShortFromBuffer();
        short qClass = getNextShortFromBuffer();

        return new DNSPacket.DNSQuery(query, qType, qClass);
    }

    private DNSPacket.DNSAnswer parseAnswer() {
        DNSPacket.DNSQuery query;

        if (queryIsAPointer(nextByte())) {
            int offset = nextByte();
            int oldIndex = this.currentBufferIndex;
            this.currentBufferIndex = offset;
            query = parseQuery();
            this.currentBufferIndex = oldIndex;
            nextByte();
            nextByte();
            nextByte();
            nextByte();
        } else {
            query = parseQuery();
        }

        int ttl = getNextIntFromBuffer();
        short dataLength = getNextShortFromBuffer();

        int address = getNextIntFromBuffer();
        return new DNSPacket.DNSAnswer(query, ttl, dataLength, address);
    }

    private boolean queryIsAPointer(byte currentByte) {
        return currentByte == ((byte) POINTER_TYPE);
    }

    private String parseQueryLabels() {
        StringBuilder stringBuilder = new StringBuilder();

        byte currentCharacter = nextByte();

        while (!nullTerminator(currentCharacter)) {
            int labelCount = currentCharacter;

            byte[] label = new byte[labelCount];
            for (int i = 0; i < labelCount; i++) {
                label[i] = nextByte();
            }
            stringBuilder.append(new String(label, 0, labelCount, StandardCharsets.UTF_8));

            currentCharacter = nextByte();

            if (!nullTerminator(currentCharacter)) {
                stringBuilder.append(".");
            }
        }

        return stringBuilder.toString();
    }

    private short getNextShortFromBuffer() {
        int upperByte = nextByte() & 0x000000ff;
        int lowerByte = nextByte() & 0x000000ff;
        return (short) ((upperByte << 8) | (lowerByte));
    }

    private int getNextIntFromBuffer() {
        int upperByte = nextByte() & 0x000000ff;
        int upperMiddle = nextByte() & 0x000000ff;
        int lowerMiddle = nextByte() & 0x000000ff;
        int lowerByte = nextByte() & 0x000000ff;
        return ((upperByte << 24) | (upperMiddle << 16) | (lowerMiddle << 8) | (lowerByte));
    }

    private byte nextByte() {
        return response[currentBufferIndex++];
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean nullTerminator(byte currentCharacter) {
        return currentCharacter == NULL_TERMINATOR;
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    private boolean booleanFromInt(int value) {
        return value == 0 ? false : true;
    }
}

// No need for this comment,
// But just in case we need it
// https://stackoverflow.com/questions/29945627/java-8-lambda-void-argument
