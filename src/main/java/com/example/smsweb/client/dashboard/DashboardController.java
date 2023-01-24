package com.example.smsweb.client.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("dashboard")
public class DashboardController {

    @GetMapping("/dashboard")
    public String index(){
        return "dashboard/index";
    }

    @GetMapping("/dashboard/create-student")
    public String createStudent(){
        return "dashboard/student/create_student";
    }

}
