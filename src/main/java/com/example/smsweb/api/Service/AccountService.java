package com.example.smsweb.api.Service;

import com.example.smsweb.Models.Account;
import com.example.smsweb.api.di.irepository.IAccount;
import com.example.smsweb.api.di.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService implements IAccount {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public void save(Account account) {
        accountRepository.save(account);
    }

    @Override
    public void update(Account account) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findOne(int id) {
        return null;
    }
}
