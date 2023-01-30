package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Subject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("subject")
public class SubjectController {

    private String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private String MAJOR_URL = "http://localhost:8080/api/major/";
    private String SEMESTER_URL = "http://localhost:8080/api/semester/";

    private RestTemplate restTemplate;
    ResponseModel listSubject;
    ResponseModel listMajor;
    ResponseModel listSemester;

    @GetMapping("/index")
    public String index(Model model) {
        listSubject = new ResponseModel();
        listMajor = new ResponseModel();
        listSemester = new ResponseModel();
        restTemplate = new RestTemplate();

        listSubject = restTemplate.getForObject(SUBJECT_URL + "list", ResponseModel.class);
        listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
        listSemester = restTemplate.getForObject(SEMESTER_URL + "list",ResponseModel.class);

        model.addAttribute("listSubject", listSubject.getData());
        model.addAttribute("listMajor", listMajor.getData());
        model.addAttribute("listSemester", listSemester.getData());
        model.addAttribute("subject", new Subject());
        return "dashboard/subject/subject_index";
    }

    @PostMapping(value = "/post")
    @ResponseBody
    public Object post(@RequestBody Subject subject) {
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
            ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "save", HttpMethod.POST, request, ResponseModel.class);
            if(response.getStatusCode().is2xxSuccessful()){
                return response;
            }else{
                return response;
            }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id){
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String,String> content = new LinkedMultiValueMap<>();
        content.add("id",String.valueOf(id));
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(content,headers);
        ResponseEntity<String> response = restTemplate.exchange(SUBJECT_URL+"delete/" + id,HttpMethod.DELETE,request,String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return "redirect:/subject/index";
        }else{
            return "redirect:/subject/index";
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@RequestBody Subject subject){
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
        ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL + "save", HttpMethod.POST, request, ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return response;
        }else{
            return response;
        }
    }
    @GetMapping("/findOne/{id}")
    @ResponseBody
    public Object findOne(@PathVariable("id") int id) {
        restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String,String> content = new LinkedMultiValueMap<>();
        content.add("id",String.valueOf(id));
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<MultiValueMap<String,String>>(content,headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(SUBJECT_URL+"findOne/"+id,HttpMethod.GET,request,ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody().getData();
        }else{
            return response;
        }
    }
}
