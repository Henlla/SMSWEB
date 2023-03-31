package com.example.smsweb.client.dashboard;

import com.example.smsweb.api.di.irepository.IMajor;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Apartment;
import com.example.smsweb.models.Major;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@MultipartConfig
@RequestMapping("dashboard/major")
@Slf4j
public class MajorController {
    @Autowired
    private IMajor service;
    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    private final String APARTMENT_URL = "http://localhost:8080/api/apartment/";
    List<Major> listMajor;
    List<Apartment> listApartment;
    RestTemplate restTemplate;

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseApartment = restTemplate.exchange(APARTMENT_URL, HttpMethod.GET, request, ResponseModel.class);
                ResponseEntity<ResponseModel> responseMajor = restTemplate.exchange(MAJOR_URL + "list", HttpMethod.GET, request, ResponseModel.class);
                model.addAttribute("listApartment", responseApartment.getBody().getData());
                model.addAttribute("listMajor", responseMajor.getBody().getData());
                return "dashboard/major/major_index";
            } else {
                return "redirect:/dashboard/login";
            }
        } catch (Exception e) {
            log.error("Index Major: " + e.getMessage());
            return e.getMessage();
        }
    }

    @GetMapping("/findApartment")
    @ResponseBody
    public Object findApartment(@CookieValue(name = "_token") String _token) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                listApartment = new ArrayList<>();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseApartment = restTemplate.exchange(APARTMENT_URL, HttpMethod.GET, request, ResponseModel.class);
                String apartmentJson = new ObjectMapper().writeValueAsString(responseApartment.getBody().getData());
                listApartment = new ObjectMapper().readValue(apartmentJson, new TypeReference<List<Apartment>>() {
                });
                return listApartment;
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Can't get Apartment", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/save")
    @ResponseBody

    public Object post(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Major major) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseMajor = restTemplate.exchange(MAJOR_URL + "findByMajorCode/" + major.getMajorCode(), HttpMethod.GET, request, ResponseModel.class);
                if (responseMajor.getStatusCode().is2xxSuccessful()) {
                    String majorJson = new ObjectMapper().writeValueAsString(responseMajor.getBody().getData());
                    Major majorResponse = new ObjectMapper().readValue(majorJson, Major.class);
                    if (majorResponse == null) {
                        MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
                        content.add("major", major);
                        HttpEntity<MultiValueMap<String, Object>> requestMajor = new HttpEntity<MultiValueMap<String, Object>>(content, headers);
                        ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "save", HttpMethod.POST, requestMajor, ResponseModel.class);
                        return response.getBody().getData();
                    } else {
                        return new ResponseEntity<String>("This curriculum was having", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<String>("Wrong curriculum format", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (HttpClientErrorException e) {
            String message = e.getResponseBodyAsString();
            return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Save Major: " + e.getMessage());
            return new ResponseEntity<String>("Create fail", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/findOne/{id}")
    @ResponseBody
    public Object findOne(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") int id) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                HttpEntity<String> request = new HttpEntity<String>(headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "findOne/" + id, HttpMethod.GET, request, ResponseModel.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (HttpClientErrorException e) {
            String message = e.getResponseBodyAsString();
            return new ResponseEntity<String>(message, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("FindOne Major: " + e.getMessage());
            return new ResponseEntity<String>("Don't find any record", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Object update(@CookieValue(name = "_token", defaultValue = "") String _token, @RequestBody Major major) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> responseMajor = restTemplate.exchange(MAJOR_URL + "findByMajorCode/" + major.getMajorCode(), HttpMethod.GET, request, ResponseModel.class);
                if (responseMajor.getStatusCode().is2xxSuccessful()) {
                    String majorJson = new ObjectMapper().writeValueAsString(responseMajor.getBody().getData());
                    Major majorResponse = new ObjectMapper().readValue(majorJson, Major.class);
                    if (majorResponse == null) {
                        MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
                        content.add("major", major);
                        HttpEntity<MultiValueMap<String, Object>> requestMajor = new HttpEntity<MultiValueMap<String, Object>>(content, headers);
                        ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "update", HttpMethod.PUT, requestMajor, ResponseModel.class);
                        return response.getBody().getData();
                    } else {
                        return new ResponseEntity<String>("The curriculum updating was have", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<String>("Can't update curriculum in this time! Please contact admin", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (HttpClientErrorException e) {
            String message = e.getResponseBodyAsString();
            return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Update Major: " + e.getMessage());
            return new ResponseEntity<String>("Update fail", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/delete/{id}")
    @ResponseBody
    public Object delete(@CookieValue(name = "_token", defaultValue = "") String _token, @PathVariable("id") String id) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
                content.add("id", String.valueOf(id));
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "delete/" + id, HttpMethod.DELETE, request, ResponseModel.class);
                return response.getBody().getData();
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (HttpClientErrorException e) {
            String message = e.getResponseBodyAsString();
            return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Delete Major: " + e.getMessage());
            return new ResponseEntity<String>("Delete fail", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/export-excel")
    public ResponseEntity<String> exportExcel(@CookieValue(name = "_token", defaultValue = "") String _token, HttpServletResponse responses) throws IOException {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<Object> request = new HttpEntity<>(headers);
                ResponseEntity<ResponseModel> response = restTemplate.exchange(MAJOR_URL + "list", HttpMethod.GET, request, ResponseModel.class);
                String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
                List<Major> listMajors = new ObjectMapper().readValue(json, new TypeReference<>() {
                });
                service.exportDataToExcel(responses, listMajors, "major_export");
                return new ResponseEntity<String>("Export data success", HttpStatus.OK);
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Export Major: " + e.getMessage());
            return new ResponseEntity<String>("Export data fail", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/import-excel")
    @ResponseBody
    public Object importExcel(@CookieValue(name = "_token") String _token, @RequestParam("file") MultipartFile file) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                String status = service.importDataToDb(file);
                if (status.equals("")) {
                    return new ResponseEntity<String>("Import data success", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>(status, HttpStatus.NO_CONTENT);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (HttpClientErrorException e) {
            String message = e.getResponseBodyAsString();
            return new ResponseEntity<String>(message, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Import Major: " + e.getMessage());
            return new ResponseEntity<String>("Import data fail", HttpStatus.BAD_REQUEST);
        }
    }
}
