package com.example.smsweb.client.dashboard;

import com.example.smsweb.jwt.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller("dashboard")
@Slf4j
public class DashboardController {

    @GetMapping("/dashboard")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token){
        try {
            JWTUtils.checkExpired(_token);
            return "dashboard/index";
        }catch (Exception ex){
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }
}
