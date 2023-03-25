package com.example.smsweb.client.dashboard;

import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Apartment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("dashboard/apartment")
@Slf4j
public class ApartmentController {
    private final String APARTMENT_URL = "http://localhost:8080/api/apartment/";

    private List<Apartment> listApartment;

    RestTemplate restTemplate;

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                return "dashboard/apartment/apartment";
            } else {
                return "redirect:/login";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "error/error";
        }
    }

    @GetMapping("/findAllApartment")
    @ResponseBody
    public Object findAllApartment(@CookieValue(name = "_token") String _token) {
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

    @PostMapping("/createApartment")
    @ResponseBody
    public Object createApartment(@CookieValue(name = "_token") String _token,
                                  @RequestBody Apartment apartment) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + _token);
                MultiValueMap<String, String> apartmentContent = new LinkedMultiValueMap<>();
                apartmentContent.add("apartment", new ObjectMapper().writeValueAsString(apartment));
                HttpEntity<MultiValueMap<String, String>> apartmentRequest = new HttpEntity<>(apartmentContent, headers);
                ResponseEntity<ResponseModel> apartmentResponse = restTemplate.exchange(APARTMENT_URL, HttpMethod.POST, apartmentRequest, ResponseModel.class);
                if (apartmentResponse.getStatusCode().is2xxSuccessful()) {
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("Save apartment fail", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Can't save Apartment", HttpStatus.NOT_FOUND);
        }
    }
}
