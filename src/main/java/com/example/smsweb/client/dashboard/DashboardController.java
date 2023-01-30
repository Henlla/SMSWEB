package com.example.smsweb.client.dashboard;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller("dashboard")
public class DashboardController {

    @GetMapping("/dashboard")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token){
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        return "dashboard/index";
    }
}
