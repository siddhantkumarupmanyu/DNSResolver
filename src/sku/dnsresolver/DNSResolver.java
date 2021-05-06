package sku.dnsresolver;

import org.apache.commons.lang.StringUtils;
import sku.dnsresolver.network.DNSSocketAddress;
import sku.dnsresolver.network.NetworkExecutor;
import sku.dnsresolver.ui.UiListener;
import sku.dnsresolver.ui.UserRequestListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

// TODO: refactor
public class DNSResolver implements UserRequestListener, DNSMessageListener {
    private final int NS_TYPE = 2;


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
            query = new DNSPacket.DNSQuery(domainName, (short) 1, (short) 1);
        } else {
            query = new DNSPacket.DNSQuery("", (short) 2, (short) 1);
        }

        DNSPacket packet = new DNSQueryBuilder()
                .setId(generateId())
                .setRecursionDesired(recursion)
                .setQueries(query)
                .build();

        queries.put(packet.id, new String[]{domainName, ""});
        executor.query(dnsSocketAddress, packet);
    }

    @Override
    public void receiveMessage(DNSMessage message) {
        DNSPacket packet = message.packet;

        String originalQuery = queries.get(packet.id)[0];

        DNSPacket.DNSQuery currentQuery = packet.queries[0];

        if (Objects.equals(originalQuery, currentQuery.query) && (packet.queries[0].qType != NS_TYPE)) {
            PacketFormatter formatter = new PacketFormatter(packet);
            uiListener.responseText(formatter.getFormattedString());
        } else if ((Objects.equals(originalQuery, currentQuery.query) && (packet.queries[0].qType == NS_TYPE))) {
            DNSPacket queryNsAddress = new DNSQueryBuilder()
                    .setId(packet.id)
                    .setRecursionDesired(false)
                    .setQueries(new DNSPacket.DNSQuery(originalQuery, (short) 1, (short) 1))
                    .build();

            executor.query(message.from, queryNsAddress);
        } else {

            if (packet.queries[0].qType == NS_TYPE) {
                DNSPacket queryNsAddress = new DNSQueryBuilder()
                        .setId(packet.id)
                        .setRecursionDesired(false)
                        .setQueries(new DNSPacket.DNSQuery(packet.answers[0].address, (short) 1, (short) 1))
                        .build();

                executor.query(message.from, queryNsAddress);

            } else {
                String queried = queries.get(packet.id)[1];
                String remaining = originalQuery.replace(queried, "");
                String[] remainingLabels = remaining.split("\\.");
                String lastLabel = remainingLabels[remainingLabels.length - 1];

                final String nextQueryLabel;

                if (StringUtils.isEmpty(queried)) {
                    nextQueryLabel = lastLabel;
                } else {
                    nextQueryLabel = lastLabel + "." + queried;
                }

                queries.get(packet.id)[1] = nextQueryLabel;

                DNSPacket nextQuery = new DNSQueryBuilder()
                        .setId(packet.id)
                        .setRecursionDesired(false)
                        .setQueries(new DNSPacket.DNSQuery(nextQueryLabel, (short) 2, (short) 1))
                        .build();


                DNSSocketAddress serverAddress = new DNSSocketAddress(packet.answers[0].address, "53");
                executor.query(serverAddress, nextQuery);
            }
        }
    }

    private short generateId() {
        return (short) ThreadLocalRandom.current().nextInt();
    }
}
