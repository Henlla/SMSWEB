package com.example.smsweb.api.service;

import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.api.di.irepository.IAccount;
import com.example.smsweb.api.di.repository.AccountRepository;
import com.example.smsweb.models.Account;
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
    AccountRepository dao;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void save(Account account) {
        try {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            dao.save(account);
        }catch (Exception e){
            throw new ErrorHandler("Cannot save data");
        }
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Account> findAll() {
        return dao.findAll();
    }

    @Override
    public Account findOne(int id) {
        return dao.findById(id).orElseThrow(()->new ErrorHandler("Can not find student with id = "+1));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = dao.findAccountByUsername(username);
        if(account==null){
            throw new UsernameNotFoundException(username + " not found ");
        }
        return account;
    }

}
