package com.optivem.kata.banking.adapters.driven.fake.givens;

import com.optivem.kata.banking.adapters.driven.fake.FakeCustomerProvider;

public class FakeCustomerProviderGiven {

    private FakeCustomerProvider provider;

    public FakeCustomerProviderGiven(FakeCustomerProvider provider) {
        this.provider = provider;
    }

    public void isBlacklisted(String nationalIdentityNumber) {
        provider.registerBlacklisted(nationalIdentityNumber);
    }
}