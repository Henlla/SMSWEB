package com.example.smsweb.api.controller;

import com.example.smsweb.dto.LoginResponse;
import com.example.smsweb.jwt.JwtTokenProvider;
import com.example.smsweb.api.di.irepository.IAccount;
import com.example.smsweb.api.generic.GenericController;
import com.example.smsweb.models.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/accounts")
@Slf4j
public class AccountController extends GenericController<Account> {
    @Autowired
    IAccount iAccount;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping(value = "/",headers = {"content-type=application/json" })
    public ResponseEntity<?> postAccount(@RequestBody Account account){
        iAccount.save(account);
        return new ResponseEntity<>(account, HttpStatus.OK);
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

}
