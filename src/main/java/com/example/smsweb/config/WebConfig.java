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
                 ,"/api/semester/**","/api/roles/**","/api/application/**","/api/application_type/**","/fcm/**","/api/device/**").permitAll()
                .requestMatchers("/dashboard/login","/login/**").permitAll()
                .requestMatchers("/dashboard").hasAnyAuthority("ADMIN","STAFF")
                .requestMatchers("/dashboard/teacher/**","/dashboard/student/**"
                        ,"/dashboard/major/**","/dashboard/subject/**","/dashboard/application/**"
                        ,"/dashboard/applicationType/**","/dashboard/news/**","/dashboard/class/**","/dashboard/attendance/**").hasAnyAuthority("STAFF","ADMIN")
                .requestMatchers("/dashboard/**").hasAnyAuthority("ADMIN")
                .requestMatchers("/css/**","/js/**","/plugins/**","/img/**").permitAll()
                .requestMatchers("/api/news/list","/api/news/get/{id}").permitAll()
                .requestMatchers("/api/accounts/changePassword/{id}","/api/profiles/get/{id}").hasAnyAuthority("STUDENT","ADMIN","STAFF","TEACHER")
                .requestMatchers("/api/students/getByProfile/{id}", "/api/teachers/get/{id}").hasAnyAuthority("STUDENT")
                .requestMatchers("/api/teachers/getByProfile/{id}", "/api/teachers/get/{id}").hasAnyAuthority("TEACHER")
                .requestMatchers("/api/students-subject/**","/api/classes/**","/api/schedules/**",
                        "/api/schedules_detail/**","/api/student-major/**").hasAnyAuthority("ADMIN","STUDENT","TEACHER","STAFF")
                .requestMatchers("/api/accounts/**","/api/profiles/**","/api/students/**","/api/teachers/**",
                        "/api/staffs/**","/api/news/**").hasAnyAuthority("ADMIN","STAFF")
                //teacher
                .requestMatchers("/api/attendance/**","/dashboard/attendance/**"
                        ,"/api/student-class/**","/teacher/**","/api/students/**")
                .permitAll()
                //student
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied")
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
