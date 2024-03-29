package com.example.smsweb.api.controller;

import com.example.smsweb.dto.LoginResponse;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JwtTokenProvider;
import com.example.smsweb.api.di.irepository.IAccount;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.models.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("api/accounts")
@Slf4j
public class AccountRestController extends GenericController<Account> {
    @Autowired
    IAccount iAccount;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping(value = "/")
    public ResponseEntity<?> postAccount(@RequestParam("account")String account) throws JsonProcessingException {
        Account accountConvert = new ObjectMapper().readValue(account, Account.class);
        iAccount.save(accountConvert);
        return new ResponseEntity<>(new ResponseModel("success",LocalTime.now().toString(),accountConvert), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAccount(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
            log.info("START method login params username = {}, password = {} :::::::::",username,password);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Account account = (Account) authentication.getPrincipal();
            String jwtToken = jwtTokenProvider.generateTokenFromAccount(account);
            log.info("Response account = {} , token = {}",account,jwtToken);
            log.info("FINISH method login :::::::::");
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse("success", jwtToken, account));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("FAILED method login :::::::::");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponse(null, null, "failed"));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getAccount(@PathVariable("id")Integer id){
        Account account = iAccount.findOne(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PutMapping("changePassword/{id}")
    public ResponseEntity<?> changePasswordAccount(@PathVariable("id") Integer id,
                                                   @RequestParam("password") String password,
                                                   @RequestParam("newPassword") String newPassword) {
        try {
            log.info("START method change password width params id= {},password ={}, newPassword= {} ",id,password,newPassword);
            Account account = iAccount.findOne(id);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(account, password)
            );
            Account getAccount = (Account) authentication.getPrincipal();
            getAccount.setPassword(newPassword);
            iAccount.save(getAccount);
            log.info("FINISH method changePasswordAccount :::::::::");
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(), getAccount));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel(e.getMessage(), LocalTime.now().toString(), null));
        }
    }

    @PutMapping("reset_password/{id}")
    public ResponseEntity<?> reset_password(@PathVariable("id") Integer id,
                                                   @RequestParam("password") String password) {
        try {
            log.info("START method reset_password width params id= {},password ={}",id,password);
            Account account = iAccount.findOne(id);
            account.setPassword(password);
            iAccount.save(account);
            log.info("FINISH method changePasswordAccount :::::::::");
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseModel("success", LocalTime.now().toString(), account));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseModel(e.getMessage(), LocalTime.now().toString(), null));
        }
    }
}
