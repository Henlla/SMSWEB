package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.LoginResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@Slf4j
public class LoginController {

    private final String ACCOUNT_URL = "http://localhost:8080/api/accounts/";

    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping("dashboard/login")
    public String login() {
        return "dashboard/login/login";
    }

    @PostMapping("dashboard/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletResponse responseHttp,
                        HttpServletRequest requestHttp,
                        Model model) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("username", username);
        params.add("password", password);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        try {
            ResponseEntity<LoginResponse> response = restTemplate.exchange(ACCOUNT_URL + "login", HttpMethod.POST, request, LoginResponse.class);
            String responseJson = new ObjectMapper().writeValueAsString(response.getBody().getData());
            String _token = response.getBody().getToken();

            UsernamePasswordAuthenticationToken authReq
                    = new UsernamePasswordAuthenticationToken(username, password);
            Authentication auth = authenticationManager.authenticate(authReq);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
            HttpSession session = requestHttp.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

            boolean hasUserRole = auth.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ADMIN") || r.getAuthority().equals("STAFF"));

            if (hasUserRole) {
                Cookie jwtTokenCookie = new Cookie("_token", _token);
                jwtTokenCookie.setSecure(true);
                jwtTokenCookie.setHttpOnly(true);
                jwtTokenCookie.setPath("/");
                responseHttp.addCookie(jwtTokenCookie);
                return "redirect:/dashboard";
            }else{
                model.addAttribute("msg", "Tài khoản không cấp phép");
                return "dashboard/login/login";
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                model.addAttribute("msg", "Đăng nhập thất bại");
                return "dashboard/login/login";
            }
            return null;
        }
    }

    @PostMapping("dashboard/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("_token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/dashboard/login";
    }
}
