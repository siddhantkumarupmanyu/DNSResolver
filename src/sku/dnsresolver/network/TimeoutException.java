package sku.dnsresolver.network;

public class TimeoutException extends NetworkException {
    public TimeoutException() {
        super("Request Timeout");
    }
}
