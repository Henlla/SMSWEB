package com.example.smsweb.client.student;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("student")
public class StudentClientController {
    @GetMapping("/index")
    public String index() {
        return "student/index";
    }
}
