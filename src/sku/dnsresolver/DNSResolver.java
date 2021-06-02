package sku.dnsresolver;

import org.apache.commons.lang.StringUtils;
import sku.dnsresolver.network.DNSSocketAddress;
import sku.dnsresolver.network.NetworkExecutor;
import sku.dnsresolver.ui.UiListener;
import sku.dnsresolver.ui.UserRequestListener;
import sku.dnsresolver.util.Defect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class DNSResolver implements UserRequestListener, DNSMessageListener {
    public static final String DEFAULT_PORT = "53";

    private final NetworkExecutor executor;
    private final UiListener uiListener;

    private final HashMap<Short, String[]> queries = new HashMap<>();
    private String requestPort;

    public DNSResolver(NetworkExecutor executor, UiListener uiListener) {
        this.executor = executor;
        this.uiListener = uiListener;
        this.requestPort = DEFAULT_PORT;
    }

    @Override
    public void resolve(String domainName, String serverIp, String port, boolean recursion) {
        DNSSocketAddress dnsSocketAddress = new DNSSocketAddress(serverIp, port);

        requestPort = port;

        DNSPacket.DNSQuery query;

        if (recursion) {
            query = new DNSPacket.DNSQuery(domainName, DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        } else {
            query = new DNSPacket.DNSQuery("", DNSPacket.TYPE_NS, DNSPacket.CLASS_1);
        }

        DNSPacket packet = new DNSQueryBuilder()
                .setId(generateId())
                .setRecursionDesired(recursion)
                .setQueries(query)
                .build();

        queries.put(packet.id, new String[]{domainName, query.query});
        executor.query(dnsSocketAddress, packet);
    }

    @Override
    public void receiveMessage(DNSMessage message) {
        DNSPacket packet = message.packet;
        String originalQueryLabels = getOriginalQueryLabels(packet.id);
        DNSPacket.DNSQuery currentQuery = packet.queries[0];

        if (isResolved(currentQuery, originalQueryLabels)) {
            notifyUi(packet);
        } else {
            NextQuery nextQuery = getNextQuery(message.from, message.packet);
            sendQuery(nextQuery.to, packet.id, nextQuery.query);
        }
    }

    private NextQuery getNextQuery(DNSSocketAddress from, DNSPacket packet) {
        String originalQueryLabels = getOriginalQueryLabels(packet.id);
        String alreadyQueriedLabels = alreadyQueriedLabels(packet.id);

        if (isNameServersResponseWithoutIp(packet)) {
            return queryForNameServerIp(from, packet);
        } else if (isIntermediateNSIpResponse(packet, originalQueryLabels, alreadyQueriedLabels)) {
            NextQuery nextQuery = queryForNextNameServers(from, packet);
            updateAlreadyQueriedLabels(packet.id, nextQuery.query.query);
            return nextQuery;
        } else if (isFinalNSIpResponse(packet, originalQueryLabels, alreadyQueriedLabels)) {
            return queryForDomainNameIp(from, packet);
        } else {
            throw new Defect("DNSResolver Error");
        }
    }

    private NextQuery queryForNameServerIp(DNSSocketAddress from, DNSPacket packet) {
        final String nameServer;
        if (packet.answerRRCount > 0) {
            nameServer = packet.answers[0].address; // first nameServer record
        } else {
            nameServer = packet.authoritativeNameServers[0].address;
        }
        return createQuery(from.ipAddress, nameServer, DNSPacket.TYPE_A);
    }

    private NextQuery queryForNextNameServers(DNSSocketAddress from, DNSPacket packet) {
        final String nameServerIp = getNameServerIp(from, packet);
        String nextQueryLabels = nextQueryLabels(packet.id, getOriginalQueryLabels(packet.id), alreadyQueriedLabels(packet.id));
        return createQuery(nameServerIp, nextQueryLabels, DNSPacket.TYPE_NS);
    }

    private NextQuery queryForDomainNameIp(DNSSocketAddress from, DNSPacket packet) {
        String nameServerIp = getNameServerIp(from, packet);
        String originalQueryLabels = getOriginalQueryLabels(packet.id);
        return createQuery(nameServerIp, originalQueryLabels, DNSPacket.TYPE_A);
    }

    private String getNameServerIp(DNSSocketAddress from, DNSPacket packet) {
        String nameServerIp;
        if (packet.authoritative) {
            nameServerIp = from.ipAddress;
        } else if (packet.answerRRCount > 0) {
            nameServerIp = packet.answers[0].address; // first nameServer record
        } else {
            nameServerIp = getAuthoritativeNSIpFromAdditional(packet.authoritativeNameServers, packet.additionalAnswers);
        }
        return nameServerIp;
    }

    private void notifyUi(DNSPacket packet) {
        PacketFormatter formatter = new PacketFormatter(packet);
        uiListener.responseText(packet.queries[0].query, formatter.getFormattedString());
    }

    private void sendQuery(DNSSocketAddress to, short id, DNSPacket.DNSQuery requestQuery) {
        DNSPacket request = new DNSQueryBuilder()
                .setId(id)
                .setRecursionDesired(false)
                .setQueries(requestQuery)
                .build();

        executor.query(to, request);
    }

    private String nextQueryLabels(short id, String originalQueryLabels, String alreadyQueriedLabels) {
        String remaining = originalQueryLabels.replace(alreadyQueriedLabels, "");
        String[] remainingLabels = remaining.split("\\.");
        String lastLabel = remainingLabels[remainingLabels.length - 1];

        if (StringUtils.isEmpty(alreadyQueriedLabels(id))) {
            return lastLabel;
        } else {
            return lastLabel + "." + alreadyQueriedLabels(id);
        }
    }

    private String getAuthoritativeNSIpFromAdditional(
            DNSPacket.DNSAnswer[] authoritativeServers,
            DNSPacket.DNSAnswer[] additionalAnswers
    ) {
        for (DNSPacket.DNSAnswer ns : authoritativeServers) {
            Optional<DNSPacket.DNSAnswer> ipAnswer = getAnswerMatchingToQueryLabels(ns.address, additionalAnswers);
            if (ipAnswer.isPresent()) {
                return ipAnswer.get().address;
            }
        }
        throw new Defect("No Matching IP in Additional Section");
    }

    private Optional<DNSPacket.DNSAnswer> getAnswerMatchingToQueryLabels(String queryString, DNSPacket.DNSAnswer[] answers) {
        return Arrays.stream(answers)
                .filter(answer -> (answer.query.query.equals(queryString)))
                .findFirst();
    }

    private boolean isNameServersResponseWithoutIp(DNSPacket packet) {
        DNSPacket.DNSQuery currentQuery = packet.queries[0];
        boolean isTypeNS = currentQuery.qType == DNSPacket.TYPE_NS;
        boolean answersExist = packet.answerRRCount > 0;
        boolean authoritativeNSExistButNotAdditional = packet.authorityRRCount > 0 && packet.additionalRRCount == 0;
        return isTypeNS && (!packet.authoritative) && (answersExist || authoritativeNSExistButNotAdditional);
    }

    private boolean isIntermediateNSIpResponse(DNSPacket packet, String originalQueryLabels, String alreadyQueriedLabels) {
        boolean isIntermediateResponse = !isFinalNSIpResponse(packet, originalQueryLabels, alreadyQueriedLabels);
        return isIntermediateResponse && isNameServersResponseWithIp(packet);
    }

    private boolean isFinalNSIpResponse(DNSPacket packet, String originalQueryLabels, String alreadyQueriedLabels) {
        return originalQueryLabels.equals(alreadyQueriedLabels) && isNameServersResponseWithIp(packet);
    }

    private boolean isNameServersResponseWithIp(DNSPacket packet) {
        boolean answersExist = packet.answerRRCount > 0;
        boolean authoritativeNSAndAdditionalExist = packet.authorityRRCount > 0 && packet.additionalRRCount > 0;
        return answersExist || authoritativeNSAndAdditionalExist || packet.authoritative;
    }

    private boolean isResolved(DNSPacket.DNSQuery currentQuery, String originalQueryLabels) {
        return originalQueryLabels.equals(currentQuery.query) && (currentQuery.qType == DNSPacket.TYPE_A);
    }

    private void updateAlreadyQueriedLabels(short id, String nextQueryLabel) {
        queries.get(id)[1] = nextQueryLabel;
    }

    private String alreadyQueriedLabels(short id) {
        return queries.get(id)[1];
    }

    private String getOriginalQueryLabels(short id) {
        return queries.get(id)[0];
    }

    private short generateId() {
        return (short) ThreadLocalRandom.current().nextInt();
    }

    private NextQuery createQuery(String toIp, String querySting, short type) {
        final DNSPacket.DNSQuery requestQuery = new DNSPacket.DNSQuery(querySting, type, DNSPacket.CLASS_1);
        return new NextQuery(new DNSSocketAddress(toIp, requestPort), requestQuery);
    }

    private static class NextQuery {
        public final DNSSocketAddress to;

        public final DNSPacket.DNSQuery query;

        private NextQuery(DNSSocketAddress to, DNSPacket.DNSQuery query) {
            this.to = to;
            this.query = query;
        }
    }
}
