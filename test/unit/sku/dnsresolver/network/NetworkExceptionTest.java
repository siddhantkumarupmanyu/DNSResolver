package sku.dnsresolver.network;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;


public class NetworkExceptionTest {

    @Test
    public void networkException() {
        Throwable throwable = new Throwable();
        NetworkException exception = new NetworkException("Network Exception Occurred", throwable);

        assertThat(exception.getMessage(), is("Network Exception Occurred"));
        assertThat(exception.getCause(), is(equalTo(throwable)));
    }

}