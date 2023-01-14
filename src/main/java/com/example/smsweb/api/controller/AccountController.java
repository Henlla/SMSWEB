package com.example.smsweb.api.controller;

import com.example.smsweb.Models.Account;
import com.example.smsweb.api.di.irepository.IAccount;
import com.example.smsweb.api.generic.GenericController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/accounts")
public class AccountController extends GenericController<Account> {
    @Autowired
    IAccount iAccount;
    @PostMapping("/postAccount")
    public ResponseEntity<?> postAccount(@RequestBody Account t){
        iAccount.save(t);
        return new ResponseEntity<>(t, HttpStatus.OK);
    }
}
