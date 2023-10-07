package com.mxd.dt.dao;

import com.mxd.dt.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface AccountDAO extends JpaRepository<Account, String> {

    @Transactional(propagation = Propagation.SUPPORTS)
    Optional<Account> findById(String id);

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    Account save(Account account);
}
