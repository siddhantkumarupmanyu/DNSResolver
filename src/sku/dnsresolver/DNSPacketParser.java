package sku.dnsresolver;

import sku.dnsresolver.util.Defect;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

    private short answerCount = 0;
    private short authoritativeNSCount = 0;
    private short additionalAnswersCount = 0;

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
        builder.setAnswerRRCount(answerCount = getNextShortFromBuffer());
        builder.setAuthorityRRCount(authoritativeNSCount = getNextShortFromBuffer());
        builder.setAdditionalRRCount(additionalAnswersCount = getNextShortFromBuffer());
    }

    private void parseBody() {
        builder.setQueries(parseQuery());
        builder.setAnswers(parseAnswers(answerCount));
        builder.setAuthoritativeNameServers(parseAnswers(authoritativeNSCount));
        builder.setAdditionalAnswers(parseAnswers(additionalAnswersCount));
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
        final String queryLabels = parseLabels();

        short qType = getNextShortFromBuffer();
        short qClass = getNextShortFromBuffer();

        return new DNSPacket.DNSQuery(queryLabels, qType, qClass);
    }

    private DNSPacket.DNSAnswer[] parseAnswers(int count) {
        DNSPacket.DNSAnswer[] answers = new DNSPacket.DNSAnswer[count];
        for (short i = 0; i < count; i++) {
            try {
                answers[i] = parseAnswer();
            } catch (NotSupportedYet e) {
                answers[i] = null;
            }
        }
        return removeNullFromAnswersArray(answers);
    }

    private DNSPacket.DNSAnswer parseAnswer() {
        final DNSPacket.DNSQuery query = parseQuery();

        int ttl = getNextIntFromBuffer();
        short dataLength = getNextShortFromBuffer();

        final String address;
        if (query.qType == DNSPacket.TYPE_A) {
            address = new StringBuilder()
                    .append(nextByte() & 0x000000ff)
                    .append(".")
                    .append(nextByte() & 0x000000ff)
                    .append(".")
                    .append(nextByte() & 0x000000ff)
                    .append(".")
                    .append(nextByte() & 0x000000ff)
                    .toString();
        } else if (query.qType == DNSPacket.TYPE_NS) {
            address = parseLabels();
        } else if (query.qType == DNSPacket.TYPE_CNAME) {
            address = parseLabels();
        } else if (query.qType == DNSPacket.TYPE_AAAA) {
            readNextBytes(dataLength);
            throw new NotSupportedYet("Ipv6");
        } else if (query.qType == DNSPacket.TYPE_SOA) {
            readNextBytes(dataLength);
            throw new NotSupportedYet("SOA");
        } else {
            throw new Defect();
        }
        return new DNSPacket.DNSAnswer(query, ttl, dataLength, address);
    }

    private void readNextBytes(int dataLength) {
        for (int i = 0; i < dataLength; i++) {
            nextByte();
        }
    }

    private String parseLabels() {
        StringBuilder stringBuilder = new StringBuilder();

        byte currentCharacter = nextByte();

        while (!nullTerminator(currentCharacter)) {
            int labelCount = currentCharacter;

            if (isAPointer((byte) labelCount)) {
                int offset = nextByte() & 0x000000ff;
                int oldIndex = this.currentBufferIndex;
                this.currentBufferIndex = offset;
                stringBuilder.append(parseLabels());
                this.currentBufferIndex = oldIndex;
                break;
            } else {
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
        }

        return stringBuilder.toString();
    }

    private boolean isAPointer(byte currentByte) {
        return currentByte == ((byte) POINTER_TYPE);
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

    private static class NotSupportedYet extends RuntimeException {
        public NotSupportedYet(String message) {
            super(message);
        }
    }

    private static DNSPacket.DNSAnswer[] removeNullFromAnswersArray(DNSPacket.DNSAnswer[] answers) {
        ArrayList<DNSPacket.DNSAnswer> list = new ArrayList<>();
        for (DNSPacket.DNSAnswer answer : answers) {
            if (answer != null) {
                list.add(answer);
            }
        }
        return list.toArray(new DNSPacket.DNSAnswer[0]);
    }
}

// No need for this comment,
// But just in case we need it
// https://stackoverflow.com/questions/29945627/java-8-lambda-void-argument
