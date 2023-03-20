package com.example.smsweb.client.dashboard;


import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.Province;
import com.example.smsweb.models.Role;
import com.example.smsweb.models.Room;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/dashboard/room")
public class RoomController {

    private final String URL_ROOM = "http://localhost:8080/api/room/";

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
    public Object create_room(@RequestParam("roomCode")String roomCode,@CookieValue(name = "_token", defaultValue = "") String _token){
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
        }catch (Exception ex){
            return "redirect:/dashboard/logout";
        }
    }
}
