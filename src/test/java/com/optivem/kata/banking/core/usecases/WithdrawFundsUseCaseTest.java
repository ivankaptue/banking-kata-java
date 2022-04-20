package com.optivem.kata.banking.core.usecases;

import com.optivem.kata.banking.core.domain.accounts.AccountNumber;
import com.optivem.kata.banking.core.domain.accounts.BankAccount;
import com.optivem.kata.banking.core.domain.exceptions.ValidationMessages;
import com.optivem.kata.banking.core.usecases.withdrawfunds.WithdrawFundsRequest;
import com.optivem.kata.banking.core.usecases.withdrawfunds.WithdrawFundsResponse;
import com.optivem.kata.banking.core.usecases.withdrawfunds.WithdrawFundsUseCase;
import com.optivem.kata.banking.infra.fake.accounts.FakeBankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static com.optivem.kata.banking.core.builders.entities.BankAccountBuilder.aBankAccount;
import static com.optivem.kata.banking.core.builders.requests.WithdrawFundsRequestBuilder.aWithdrawFundsRequest;
import static com.optivem.kata.banking.core.common.Assertions.assertResponse;
import static com.optivem.kata.banking.core.common.Assertions.assertThrowsValidationException;
import static com.optivem.kata.banking.core.common.MethodSources.NON_POSITIVE_INTEGERS;
import static com.optivem.kata.banking.core.common.MethodSources.NULL_EMPTY_WHITESPACE;
import static org.assertj.core.api.Assertions.assertThat;

class WithdrawFundsUseCaseTest {

    private FakeBankAccountRepository repository;
    private WithdrawFundsUseCase useCase;

    @BeforeEach
    void init() {
        this.repository = new FakeBankAccountRepository();
        this.useCase = new WithdrawFundsUseCase(repository);
    }

    @ParameterizedTest
    @MethodSource
    void should_withdraw_funds_given_valid_request(String accountNumber, int initialBalance, int amount, int expectedFinalBalance) {
        givenBankAccount(accountNumber, initialBalance);

        var request = aWithdrawFundsRequest()
                .accountNumber(accountNumber)
                .amount(amount)
                .build();

        var expectedResponse = new WithdrawFundsResponse();
        expectedResponse.setBalance(expectedFinalBalance);

        assertSuccess(request, expectedResponse);

        assertContainsBankAccount(accountNumber, expectedFinalBalance);
    }

    private static Stream<Arguments> should_withdraw_funds_given_valid_request() {
        return Stream.of(Arguments.of("GB10BARC20040184197751", 70, 30, 40),
                Arguments.of("GB36BMFK75394735916876", 100, 100, 0));
    }

    @ParameterizedTest
    @MethodSource(NULL_EMPTY_WHITESPACE)
    void should_throw_exception_given_empty_account_number(String accountNumber) {
        var request = aWithdrawFundsRequest()
                .accountNumber(accountNumber)
                .build();

        assertThrows(request, ValidationMessages.ACCOUNT_NUMBER_EMPTY);
    }

    @Test
    void should_throw_exception_given_non_existent_account_number() {
        var request = aWithdrawFundsRequest()
                .build();

        assertThrows(request, ValidationMessages.ACCOUNT_NUMBER_NOT_EXIST);
    }

    @ParameterizedTest
    @MethodSource(NON_POSITIVE_INTEGERS)
    void should_throw_exception_given_non_positive_amount(int amount) {
        var request = aWithdrawFundsRequest()
                .amount(amount)
                .build();

        assertThrows(request, ValidationMessages.NON_POSITIVE_TRANSACTION_AMOUNT);
    }

    @Test
    void should_throw_exception_given_insufficient_funds() {
        var accountNumber = "GB10BARC20040184197751";
        var balance = 140;
        var amount = 141;

        givenBankAccount(accountNumber, balance);

        var request = aWithdrawFundsRequest()
                .accountNumber(accountNumber)
                .amount(amount)
                .build();

        assertThrows(request, ValidationMessages.INSUFFICIENT_FUNDS);

        assertContainsBankAccount(accountNumber, balance);
    }

    private void assertSuccess(WithdrawFundsRequest request, WithdrawFundsResponse expectedResponse) {
        assertResponse(useCase, request, expectedResponse);
    }

    private void assertThrows(WithdrawFundsRequest request, String message) {
        assertThrowsValidationException(useCase, request, message);
    }

    private Optional<BankAccount> findBankAccount(String accountNumber) {
        return repository.find(new AccountNumber(accountNumber));
    }

    private void givenBankAccount(String accountNumber, int balance) {
        var bankAccount = aBankAccount()
                .accountNumber(accountNumber)
                .balance(balance)
                .build();

        repository.add(bankAccount);
    }

    private void assertContainsBankAccount(String accountNumber, int balance) {
        var expectedBankAccount = aBankAccount()
                .accountNumber(accountNumber)
                .balance(balance)
                .build();

        var retrievedBankAccount = findBankAccount(accountNumber);
        assertThat(retrievedBankAccount).usingRecursiveComparison().isEqualTo(Optional.of(expectedBankAccount));
    }
}