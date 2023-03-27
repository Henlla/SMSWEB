package com.example.smsweb.client.dashboard;


import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.Province;
import com.example.smsweb.models.Role;
import com.example.smsweb.models.Room;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.Http;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/dashboard/room")
public class RoomController {

    private final String URL_ROOM = "http://localhost:8080/api/room/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";

    @GetMapping("/index")
    public String index(Model model, @CookieValue(name = "_token", defaultValue = "") String _token){
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            List<Room> listRoom = restTemplate.getForObject(URL_ROOM, ArrayList.class);
            String json = new ObjectMapper().writeValueAsString(listRoom);
            List<Room> l = new ObjectMapper().readValue(json, new TypeReference<List<Room>>(){});
            model.addAttribute("listRoom", l);
            return "dashboard/room/index";
        }catch (Exception ex){
            return "redirect:/dashboard/logout";
        }
    }

    @PostMapping("/create_room")
    @ResponseBody
    public Object create_room(@RequestParam("roomCode")String roomCode,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String,Object> params = new LinkedMultiValueMap<>();
            Room room = new Room();
            room.setRoomCode(roomCode);
            String roomJson = new ObjectMapper().writeValueAsString(room);
            params.add("room",roomJson);
            HttpEntity<Object> request = new HttpEntity<>(params);
            restTemplate.exchange(URL_ROOM, HttpMethod.POST,request,String.class);
            return "success";
        }catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }

    @PostMapping("/details")
    @ResponseBody
    public Object details(@RequestParam("id")Integer id,@CookieValue(name = "_token", defaultValue = "") String _token){
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization","Bearer "+_token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL+"findClassByRoom/"+id,HttpMethod.GET,request,ResponseModel.class);
            return response.getBody().getData();
        }catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }

    @PostMapping("checkShiftRoom")
    @ResponseBody
    public Object checkShiftRoom(@RequestParam("shift")String shift,@CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization","Bearer "+_token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            List<Room> listRoom = restTemplate.getForObject(URL_ROOM, ArrayList.class);
            String json = new ObjectMapper().writeValueAsString(listRoom);
            List<Room> l = new ObjectMapper().readValue(json, new TypeReference<List<Room>>(){});
            List<Room> roomList = new ArrayList<>();
            for (Room room:l){
                boolean flag = false;
                ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL+"findClassByRoom/"+room.getId(),HttpMethod.GET,request,ResponseModel.class);
                String jsonClassList = new ObjectMapper().writeValueAsString(response.getBody().getData());
                List<Classses> list = new ObjectMapper().readValue(jsonClassList, new TypeReference<List<Classses>>() {
                });
                if(list == null){
                    flag = false;
                }else{
                    boolean isCheck = list.stream().anyMatch(classses -> classses.getShift().equals(shift));
                    if(isCheck){
                        flag = true;
                    }
                }
                if(!flag){
                    roomList.add(room);
                }
            }
            return roomList;
        }catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }
}
