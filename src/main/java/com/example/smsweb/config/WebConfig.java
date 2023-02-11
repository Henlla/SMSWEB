package com.example.smsweb.config;


import com.example.smsweb.api.service.AccountService;
import com.example.smsweb.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebConfig {
    @Autowired
    private AccountService accountService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder builder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(accountService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .requestMatchers("/api/major/**","/api/subject/**","/api/provinces/**",
                 "/api/districts/**","/api/accounts/login","/api/wards/**"
                        ,"/api/semester/**","/api/roles/**","/api/application/**","/api/application_type/**").permitAll()
                .requestMatchers("/dashboard/login").permitAll()
                .requestMatchers("/dashboard").hasAnyAuthority("ADMIN","STAFF")
                .requestMatchers("/dashboard/teacher/**","/dashboard/student/**"
                        ,"/dashboard/major/**","/dashboard/subject/**","/dashboard/application/**").hasAnyAuthority("STAFF","ADMIN")
                .requestMatchers("/dashboard/**").hasAnyAuthority("ADMIN")
                .requestMatchers("/css/**","/js/**","/plugins/**","/img/**").permitAll()
                .requestMatchers("/api/news/list","/api/news/get/{id}").permitAll()
                .requestMatchers("/api/accounts/changePassword/{id}").hasAnyAuthority("STUDENT","ADMIN","STAFF")
                .requestMatchers("/api/accounts/**","/api/profiles/**","/api/students/**",
                 "/api/students-subject/**","/api/student-major/**","/api/teachers/**","/api/staffs/**","/api/news/**","/api/classes/**").hasAnyAuthority("ADMIN","STAFF")

                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied")
//                .and()
//                .formLogin()
//                .loginPage("/dashboard/login")
//                .defaultSuccessUrl("/dashboard", true)
//                .usernameParameter("username")
//                .passwordParameter("password")
//                .failureUrl("/dashboard/login?error=true")
                .and()
                .logout()
                .logoutUrl("/dashboard/logout")
                .logoutSuccessUrl("/dashboard/login")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID","_token")
                .and().addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
