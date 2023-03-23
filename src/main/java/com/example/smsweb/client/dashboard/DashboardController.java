package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.Charts.CoutChart;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller("dashboard")
@Slf4j
public class DashboardController {
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";
    private final String ROOM_URL = "http://localhost:8080/api/room/";
    RestTemplate restTemplate;

    @GetMapping("/dashboard")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);

                ResponseEntity<ArrayList> responseStudent = restTemplate.exchange(STUDENT_URL + "list", HttpMethod.GET, request, ArrayList.class);
                ResponseEntity<ArrayList> responseTeacher = restTemplate.exchange(TEACHER_URL + "list",HttpMethod.GET,request,ArrayList.class);
                ResponseEntity<ArrayList> responseClass = restTemplate.exchange(CLASS_URL + "list",HttpMethod.GET,request,ArrayList.class);
                ResponseEntity<ArrayList> responseRoom = restTemplate.exchange(ROOM_URL,HttpMethod.GET,request,ArrayList.class);
                long studentCount = responseStudent.getBody().stream().count();
                long teacherCount = responseTeacher.getBody().stream().count();
                long classCount = responseClass.getBody().stream().count();
                long roomCount = responseRoom.getBody().stream().count();
                CoutChart countChart = new CoutChart();
                countChart.setTotalStudent(studentCount);
                countChart.setTotalTeacher(teacherCount);
                countChart.setTotalClass(classCount);
                countChart.setTotalRoom(roomCount);
                model.addAttribute("chartTotal",countChart);
                return "dashboard/home";
            } else {
                return "redirect:/dashboard/logout";
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }

    @GetMapping("/dashboard/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/dashboard/login";
    }
}
