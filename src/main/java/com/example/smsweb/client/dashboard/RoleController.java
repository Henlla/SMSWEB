package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.models.Role;
import com.example.smsweb.models.Subject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("dashboard/role/")
public class RoleController {
    private final String ROLE_URL = "http://localhost:8080/api/roles/";

    @GetMapping("index-role")
    public String index(Model model) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Role> listRole = restTemplate.getForObject(ROLE_URL+"list",List.class);
        String json = objectMapper.writeValueAsString(listRole);
        List<Role> listRoleConvert = objectMapper.readValue(json, new TypeReference<List<Role>>() {});
        model.addAttribute("listRole",listRoleConvert.stream().filter(role -> !role.getRoleName().equals("ADMIN") &&!role.getRoleName().equals("STUDENT")&&!role.getRoleName().equals("TEACHER") ));
        return "/dashboard/role/role_index";
    }

    @PostMapping("create_role")
    @ResponseBody
    public Object create_role(@RequestBody Role role){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
        content.add("role_name",role.getRoleName());
        content.add("role_description",role.getRoleDescription());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(content, headers);
        ResponseEntity<ResponseModel> response = restTemplate.exchange(ROLE_URL, HttpMethod.POST, request, ResponseModel.class);
        if(response.getStatusCode().is2xxSuccessful()){
            return response;
        }else{
            return response;
        }
    }
}
