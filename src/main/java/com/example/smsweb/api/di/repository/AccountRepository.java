package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.Account;
import com.example.smsweb.api.generic.RepositoryService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends RepositoryService<Account,Integer> {
     @Query("SELECT a FROM Account a WHERE a.username =:username")
     Account findAccountByUsername(@Param("username") String username);
}
