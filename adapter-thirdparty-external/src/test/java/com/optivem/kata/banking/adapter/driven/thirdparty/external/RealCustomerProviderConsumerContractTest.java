package com.optivem.kata.banking.adapter.driven.thirdparty.external;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "customer-provider", hostInterface = "localhost")
public class RealCustomerProviderConsumerContractTest {

    private static String BLACKLISTED_ID = "ABC_1001";
    private static String WHITELISTED_ID = "ABC_1002";
    private static String NON_EXISTENT_ID = "DEF_1002";

    @Pact(consumer = "banking-consumer")
    public RequestResponsePact createPactForBlacklistedCustomer(PactDslWithProvider builder) {
        var customerId = BLACKLISTED_ID;
        var body = new PactDslJsonBody()
                .stringType("id", customerId)
                .booleanType("blacklisted", true);

        var pathFormat = "/customers/%s";
        var path = String.format(pathFormat, customerId);

        return builder.given("GET User: customer ABC_1001 is blacklisted")
                .uponReceiving("A request for blacklisted customer ABC_1001")
                .path(path)
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(body)
                .toPact();
    }

    @Pact(consumer = "banking-consumer")
    public RequestResponsePact createPactForWhitelistedCustomer(PactDslWithProvider builder) {
        var customerId = WHITELISTED_ID;
        var body = new PactDslJsonBody()
                .stringType("id", customerId)
                .booleanType("blacklisted", false);

        var pathFormat = "/customers/%s";
        var path = String.format(pathFormat, customerId);

        return builder.given("GET User: customer ABC_1002 is whitelisted")
                .uponReceiving("A request for whitelisted customer ABC_1002")
                .path(path)
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(body)
                .toPact();
    }

    @Pact(consumer = "banking-consumer")
    public RequestResponsePact createPactForNonExistentCustomer(PactDslWithProvider builder) {
        var customerId = NON_EXISTENT_ID;

        var pathFormat = "/customers/%s";
        var path = String.format(pathFormat, customerId);

        return builder.given("GET User: customer DEF_1002 does not exist")
                .uponReceiving("A request for non-existent customer DEF_1002")
                .path(path)
                .method("GET")
                .willRespondWith()
                .status(404)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPactForBlacklistedCustomer")
    public void should_return_true_when_user_is_blacklisted(MockServer mockServer) {
        var url = mockServer.getUrl();
        var provider = new RealCustomerProvider(url);
        var nationalIdentityNumber = BLACKLISTED_ID;
        var isBlacklisted = provider.isBlacklisted(nationalIdentityNumber);
        assertThat(isBlacklisted).isTrue();
    }

    @Test
    @PactTestFor(pactMethod = "createPactForWhitelistedCustomer")
    public void should_return_true_when_user_is_whitelisted(MockServer mockServer) {
        var url = mockServer.getUrl();
        var provider = new RealCustomerProvider(url);
        var nationalIdentityNumber = WHITELISTED_ID;
        var isBlacklisted = provider.isBlacklisted(nationalIdentityNumber);
        assertThat(isBlacklisted).isFalse();
    }

    @Test
    @PactTestFor(pactMethod = "createPactForNonExistentCustomer")
    public void should_return_false_when_user_not_exists(MockServer mockServer) {
        var url = mockServer.getUrl();
        var provider = new RealCustomerProvider(url);
        var nationalIdentityNumber = NON_EXISTENT_ID;
        var exists = provider.isBlacklisted(nationalIdentityNumber);
        assertThat(exists).isFalse();
    }
}