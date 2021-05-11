package sku.dnsresolver;

import org.apache.commons.lang.StringUtils;
import sku.dnsresolver.network.DNSSocketAddress;
import sku.dnsresolver.network.NetworkExecutor;
import sku.dnsresolver.ui.UiListener;
import sku.dnsresolver.ui.UserRequestListener;
import sku.dnsresolver.util.Defect;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DNSResolver implements UserRequestListener, DNSMessageListener {
    public static final String DEFAULT_PORT = "53";

    private final NetworkExecutor executor;
    private final UiListener uiListener;

    private final HashMap<Short, String[]> queries = new HashMap<>();

    public DNSResolver(NetworkExecutor executor, UiListener uiListener) {
        this.executor = executor;
        this.uiListener = uiListener;
    }

    @Override
    public void resolve(String domainName, String serverIp, String port, boolean recursion) {
        DNSSocketAddress dnsSocketAddress = new DNSSocketAddress(serverIp, port);

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
        DNSPacket.DNSQuery currentQuery = packet.queries[0];

        if (isNameServersResponse(currentQuery)) {
            return queryForNameServerIp(from, packet);
        } else if (isIntermediateNameServersIpResponse(originalQueryLabels, alreadyQueriedLabels)) {
            NextQuery nextQuery = queryForNextNameServers(packet);
            updateAlreadyQueriedLabels(packet.id, nextQuery.query.query);
            return nextQuery;
        } else if (isFinalNameServersIpResponse(originalQueryLabels, alreadyQueriedLabels)) {
            return queryForDomainNameIp(packet);
        } else {
            throw new Defect("DNSResolver Error");
        }
    }

    private NextQuery queryForNameServerIp(DNSSocketAddress from, DNSPacket packet) {
        String nameServer = packet.answers[0].address; // first nameServer record
        final DNSPacket.DNSQuery requestQuery = new DNSPacket.DNSQuery(nameServer, DNSPacket.TYPE_A, DNSPacket.CLASS_1);
        return new NextQuery(from, requestQuery);
    }

    private NextQuery queryForNextNameServers(DNSPacket packet) {
        String nameServerIp = packet.answers[0].address; // first nameServer ip
        final DNSPacket.DNSQuery requestQuery = new DNSPacket.DNSQuery(
                nextQueryLabels(packet.id, getOriginalQueryLabels(packet.id), alreadyQueriedLabels(packet.id)),
                DNSPacket.TYPE_NS,
                DNSPacket.CLASS_1
        );
        return new NextQuery(new DNSSocketAddress(nameServerIp, DEFAULT_PORT), requestQuery);
    }

    private NextQuery queryForDomainNameIp(DNSPacket packet) {
        String nameServerIp = packet.answers[0].address; // first nameServer ip

        final DNSPacket.DNSQuery requestQuery =
                new DNSPacket.DNSQuery(getOriginalQueryLabels(packet.id), DNSPacket.TYPE_A, DNSPacket.CLASS_1);

        return new NextQuery(new DNSSocketAddress(nameServerIp, DEFAULT_PORT), requestQuery);
    }

    private void notifyUi(DNSPacket packet) {
        PacketFormatter formatter = new PacketFormatter(packet);
        uiListener.responseText(formatter.getFormattedString());
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

    private boolean isNameServersResponse(DNSPacket.DNSQuery currentQuery) {
        return currentQuery.qType == DNSPacket.TYPE_NS;
    }

    private boolean isIntermediateNameServersIpResponse(String originalQueryLabels, String alreadyQueriedLabels) {
        return !isFinalNameServersIpResponse(originalQueryLabels, alreadyQueriedLabels);
    }

    private boolean isFinalNameServersIpResponse(String originalQueryLabels, String alreadyQueriedLabels) {
        return originalQueryLabels.equals(alreadyQueriedLabels);
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

    private static class NextQuery {
        public final DNSSocketAddress to;
        public final DNSPacket.DNSQuery query;

        private NextQuery(DNSSocketAddress to, DNSPacket.DNSQuery query) {
            this.to = to;
            this.query = query;
        }
    }
}
