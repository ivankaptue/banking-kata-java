package com.optivem.kata.banking.adapter.driven.fake;

import com.optivem.kata.banking.adapter.driven.BankAccountStorageTest;
import com.optivem.kata.banking.adapter.driven.generation.random.RandomAccountIdGenerator;
import com.optivem.kata.banking.adapter.driven.generation.random.RandomAccountNumberGenerator;

public class FakeBankAccountStorageTest extends BankAccountStorageTest {
    public FakeBankAccountStorageTest() {
        super(new FakeBankAccountStorage(), new RandomAccountIdGenerator(), new RandomAccountNumberGenerator());
    }
}