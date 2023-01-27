package com.example.smsweb.client.dashboard;

import com.example.smsweb.models.Major;
import com.example.smsweb.models.Semester;
import com.example.smsweb.models.Subject;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("subject")
public class SubjectController {

    private String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private String MAJOR_URL = "http://localhost:8080/api/major/";
    private String SEMESTER_URL = "http://localhost:8080/api/semester/";

    private RestTemplate restTemplate;
    List<Subject> listSubject;
    List<Major> listMajor;
    List<Semester> listSemester;

    @GetMapping("/index")
    public String index(Model model) {
        listSubject = new ArrayList<>();
        listMajor = new ArrayList<>();
        listSemester = new ArrayList<>();
        restTemplate = new RestTemplate();

        ResponseEntity<List<Subject>> responseSubject = restTemplate.exchange(SUBJECT_URL + "list", HttpMethod.GET, null, new ParameterizedTypeReference<List<Subject>>() {
        });
        listSubject = responseSubject.getBody();
        ResponseEntity<List<Major>> responseMajor = restTemplate.exchange(MAJOR_URL + "list", HttpMethod.GET, null, new ParameterizedTypeReference<List<Major>>() {
        });
        listMajor = responseMajor.getBody();
        ResponseEntity<List<Semester>> responseSemester = restTemplate.exchange(SEMESTER_URL + "list", HttpMethod.GET, null, new ParameterizedTypeReference<List<Semester>>() {
        });
        listSemester = responseSemester.getBody();

        model.addAttribute("listSubject", listSubject);
        model.addAttribute("listMajor", listMajor);
        model.addAttribute("listSemester", listSemester);
        model.addAttribute("subject", new Subject());
        return "dashboard/subject/subject_index";
    }

    @PostMapping(value = "/post")
    @ResponseBody
    public String post(@RequestBody Subject subject) {
            restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            content.add("subjectCode",subject.getSubjectCode());
            content.add("subjectName",subject.getSubjectName());
            content.add("fee",String.valueOf(subject.getFee()));
            content.add("semesterId",String.valueOf(subject.getSemesterId()));
            content.add("majorId",String.valueOf(subject.getMajorId()));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
            ResponseEntity<String> response = restTemplate.exchange(SUBJECT_URL + "save", HttpMethod.POST, request, String.class);
            if(response.getStatusCode().is2xxSuccessful()){
                return "redirect:/subject/index";
            }else{
                return "dashboard/subject/subject_index";
            }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model){
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String,String> content = new LinkedMultiValueMap<>();
        content.add("id",String.valueOf(id));
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(content,headers);
        ResponseEntity<String> response = restTemplate.exchange(SUBJECT_URL+"delete/" + id,HttpMethod.DELETE,request,String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return "redirect:/subject/index";
        }else{
            model.addAttribute("msg",response.getBody());
            return "dashboard/subject/subject_index";
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public String update(@RequestBody Subject subject){
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        content.add("id",String.valueOf(subject.getId()));
        content.add("subjectCode",subject.getSubjectCode());
        content.add("subjectName",subject.getSubjectName());
        content.add("fee",String.valueOf(subject.getFee()));
        content.add("slot",String.valueOf(subject.getSlot()));
        content.add("semesterId",String.valueOf(subject.getSemesterId()));
        content.add("majorId",String.valueOf(subject.getMajorId()));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
        ResponseEntity<String> response = restTemplate.exchange(SUBJECT_URL + "save", HttpMethod.POST, request, String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return "success";
        }else{
            return response.getBody();
        }
    }
    @GetMapping("/findOne/{id}")
    @ResponseBody
    public String findOne(@PathVariable("id") int id) {
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String,String> content = new LinkedMultiValueMap<>();
        content.add("id",String.valueOf(id));
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(content,headers);
        ResponseEntity<String> response = restTemplate.exchange(SUBJECT_URL+"findOne/"+id,HttpMethod.GET,request,String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }else{
            return response.getBody();
        }
    }
}
