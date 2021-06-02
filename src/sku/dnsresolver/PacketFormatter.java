package sku.dnsresolver;

public class PacketFormatter {

    private final static String listItemPrefix = "+-- ";
    private final static String startingChar = "|";
    private final static String keyValueSeparator = ": ";

    private final DNSPacket packet;
    CustomStringBuilder builder;

    public PacketFormatter(DNSPacket packet) {
        this.packet = packet;
        this.builder = new CustomStringBuilder();
    }

    public String getFormattedString() {
        format();
        return builder.toString();
    }

    private void format() {
        formatHeader();
        formatBody();
    }

    private void formatHeader() {
        builder.appendKeyValuePairWithoutNewLine("id", packet.id);
        builder.appendKeyValuePair("response", packet.response);
        builder.appendKeyValuePair("opcode", packet.opCode);
        builder.appendKeyValuePair("authoritative", packet.authoritative);
        builder.appendKeyValuePair("truncated", packet.truncated);
        builder.appendKeyValuePair("recursion desired", packet.recursionDesired);
        builder.appendKeyValuePair("recursion available", packet.recursionAvailable);
        builder.appendKeyValuePair("z", packet.z);
        builder.appendKeyValuePair("answer authenticated", packet.answerAuthenticated);
        builder.appendKeyValuePair("non-authenticated data", packet.nonAuthenticatedData);
        builder.appendKeyValuePair("reply code", packet.replyCode);
        builder.appendKeyValuePair("question count", packet.questionCount);
        builder.appendKeyValuePair("answers count", packet.answerRRCount);
        builder.appendKeyValuePair("authority RR count", packet.authorityRRCount);
        builder.appendKeyValuePair("additional RR count", packet.additionalRRCount);
    }

    private void formatBody() {
        formatQueries();
        formatAnswers("answers", "answer", packet.answerRRCount, packet.answers);
        formatAnswers("authoritative NameServers", "nameserver", packet.authorityRRCount, packet.authoritativeNameServers);
        formatAnswers("additional section", "answer", packet.additionalRRCount, packet.additionalAnswers);
    }

    private void formatQueries() {
        builder.appendString("queries");
        FormatterOptions options = new FormatterOptions("", listItemPrefix, keyValueSeparator, 0);
        builder.appendListItem(options, "query", 1);
        formatQuery(packet.queries[0], options, true);
    }

    private void formatQuery(DNSPacket.DNSQuery query, FormatterOptions parentOptions, boolean isLast) {
        String sChar = isLast ? " " : startingChar;
        FormatterOptions options =
                new FormatterOptions(sChar, listItemPrefix, keyValueSeparator, parentOptions.space + 3);
        builder.appendListItem(options, "label", query.query);
        builder.appendListItem(options, "type", query.qType);
        builder.appendListItem(options, "class", query.qClass);
    }

    private void formatAnswers(String section, String sectionKey, short answersCount, DNSPacket.DNSAnswer[] answers) {
        builder.appendString(section);
        for (short i = 0; i < answersCount; i++) {
            FormatterOptions options = new FormatterOptions("", listItemPrefix, keyValueSeparator, 0);
            int position = i + 1;
            builder.appendListItem(options, sectionKey, position);
            formatAnswer(answers[i], options, position == answersCount);
        }
    }

    private void formatAnswer(DNSPacket.DNSAnswer answer, FormatterOptions parentOptions, boolean isLast) {
        String sChar = isLast ? " " : startingChar;
        FormatterOptions options =
                new FormatterOptions(sChar, listItemPrefix, keyValueSeparator, parentOptions.space + 3);

        builder.appendListItem(options, "label", answer.query.query);
        builder.appendListItem(options, "type", answer.query.qType);
        builder.appendListItem(options, "class", answer.query.qClass);
        builder.appendListItem(options, "ttl", answer.timeToLive);
        builder.appendListItem(options, "length", answer.dataLength);
        builder.appendListItem(options, "address", answer.address);

        if (!isLast) {
            builder.appendString(startingChar);
        }
    }

    // This class is more of a wrapper than a decorator
    private static class CustomStringBuilder {

        private final StringBuilder builder = new StringBuilder();

        public void appendKeyValuePairWithoutNewLine(String key, Object value) {
            builder.append(key).append(keyValueSeparator).append(value);
        }

        public void appendKeyValuePair(String key, Object value) {
            appendNewLine();
            appendKeyValuePairWithoutNewLine(key, value);
        }

        public void appendString(String str) {
            appendNewLine();
            builder.append(str);
        }

        public void appendListItem(FormatterOptions options, String key, Object value) {
            appendNewLine();
            appendListItemWithoutNewLine(options, key, value);
        }

        public void appendListItemWithoutNewLine(FormatterOptions options, String key, Object value) {
            String spaceStr = getStringWithSpaces(options.space);
            builder.append(options.startingChar).append(spaceStr).append(options.listItemPrefix).append(key);
            builder.append(options.keyValueSeparator).append(value);
        }

        public void appendNewLine() {
            builder.append("\n");
        }

        public String toString() {
            return builder.toString();
        }

        private String getStringWithSpaces(int space) {
            return new String(new char[space]).replace("\0", " ");
        }
    }

    private static class FormatterOptions {
        private final String startingChar;
        private final String listItemPrefix;
        private final String keyValueSeparator;
        private final int space;

        private FormatterOptions(String startingChar, String listItemPrefix, String keyValueSeparator, int space) {
            this.startingChar = startingChar;
            this.listItemPrefix = listItemPrefix;
            this.keyValueSeparator = keyValueSeparator;
            this.space = space;
        }

    }
}
