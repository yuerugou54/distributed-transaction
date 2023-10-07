package com.mxd.dt.service;

import com.mxd.dt.dao.AccountDAO;
import com.mxd.dt.dto.Transfer;
import com.mxd.dt.entity.Account;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@ConfigurationProperties(prefix = "bankcode-url")
public class AccountService {

    @Setter
    private Map<String, String> map = new HashMap<>();

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${bank.code}")
    @Setter
    private String defaultBankCode;

    @Transactional(propagation = Propagation.REQUIRED)
    public Account directTransfer(String fromAccountNumber, Transfer transfer) {

        Account account = accountDAO.getOne(fromAccountNumber);

        if (!map.containsKey(transfer.getBankCode().toUpperCase())) {
            throw new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE, "No Bank Code Configuration Found.");
        }

        if (account.getBalance().subtract(transfer.getAmount()).doubleValue() < 0) {
            throw new HttpClientErrorException(HttpStatus.EXPECTATION_FAILED, "Insufficient Funds.");
        }

        if (transfer.getBankCode().equalsIgnoreCase(defaultBankCode)) {

            if (fromAccountNumber.equals(transfer.getAccountNumber())) {
                throw new HttpClientErrorException(HttpStatus.EXPECTATION_FAILED, "Same Account Transaction.");
            }

            return directDeposit(transfer.getAccountNumber(), transfer);
        }

        StringBuilder url = new StringBuilder(map.get(transfer.getBankCode().toUpperCase()))
                .append("/account/").append(transfer.getAccountNumber())
                .append("/direct-deposit");

        ResponseEntity<Account> response = restTemplate.postForEntity(url.toString(), transfer, Account.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            account.setBalance(account.getBalance().subtract(transfer.getAmount()));
            accountDAO.save(account);
        }
        return account;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Account directDeposit(String fromAccountNumber, Transfer transfer) {
        Optional<Account> accountOptional = accountDAO.findById(fromAccountNumber);

        if (!accountOptional.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.resolve(420), "Depositing Account Not Found.");
        }

        Account account = accountOptional.get();
        account.setBalance(account.getBalance().add(transfer.getAmount()));
        accountDAO.save(account);
        return account;
    }

}
