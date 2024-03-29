package com.example.smsweb.jwt;


import com.example.smsweb.api.service.AccountService;
import com.example.smsweb.models.Account;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AccountService accountService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getTokenFromHeader(request);
            if(StringUtils.hasText(jwt) && jwtTokenProvider.validationToken(jwt)){
                Integer accountId = jwtTokenProvider.generateAccountIdFromToken(jwt);
                Account account = accountService.findOne(accountId);
                if(account!=null){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                            = new UsernamePasswordAuthenticationToken(account,null,account.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }catch (ExpiredJwtException e){
            log.error(e.getMessage());
        }
        filterChain.doFilter(request,response);
    }

    public String getTokenFromHeader(HttpServletRequest request){
        String bearToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearToken)&& bearToken.startsWith("Bearer ")){
            return bearToken.substring(7);
        }
        return null;
    }
}
