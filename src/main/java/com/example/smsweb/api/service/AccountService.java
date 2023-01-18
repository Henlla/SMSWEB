package com.example.smsweb.api.service;

import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.models.Account;
import com.example.smsweb.api.di.irepository.IAccount;
import com.example.smsweb.api.di.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService implements IAccount , UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void save(Account account) {
        try {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            accountRepository.save(account);
        }catch (Exception e){
            throw new ErrorHandler("Cannot save data");
        }
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
        return accountRepository.findById(id).orElseThrow(()->new ErrorHandler("Can not find student with id = "+1));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByUsername(username);
        if(account==null){
            throw new UsernameNotFoundException(username + " not found ");
        }
        return account;
    }

}
