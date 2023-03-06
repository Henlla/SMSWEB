package com.example.smsweb.client.teacher;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("teacher")
public class TeacherClientController {

    @GetMapping("/index")
    public String index() {
        return "teacher/index";
    }

}
