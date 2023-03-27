package com.example.smsweb.client.dashboard;

import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.dto.Charts.CoutChart;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.dto.RoomScheduleViewModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.StreamHelper;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller("dashboard")
@Slf4j
public class DashboardController {
    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    private final String PROVINCE_URL = "http://localhost:8080/api/provinces/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String STUDENT_SUBJECT_URL = "http://localhost:8080/api/student-subject/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String ROOM_URL = "http://localhost:8080/api/room/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";
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

    @PostMapping("/dashboard/view_room_active") @ResponseBody
    public String view_room_active(@CookieValue(value = "_token", defaultValue = "")String _token,
                                   @RequestParam(value = "date", required = false)String inputDate){
        try{
            if(JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/dashboard/logout";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> requestGET = new HttpEntity<>(headers);
            ObjectMapper objectMapper = new ObjectMapper();

            HttpEntity<MultiValueMap<String, Object>> requestPOST;
            MultiValueMap<String, Object> content;

            //Processing @RequestParam
            LocalDate date;
            if (inputDate.isEmpty() || inputDate.isBlank()){
                date = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }else {
                date = LocalDate.parse(inputDate);
            }

            //decare var
            List<Schedule> scheduleList = new ArrayList<>();
            List<ScheduleDetail> scheduleDetailList;
            List<Classses> classesList = new ArrayList<>();

            //Get All room
            HttpEntity<String> responseRoom = restTemplate.exchange(
                    ROOM_URL, HttpMethod.GET, requestGET, String.class);
            List<Room> roomList = objectMapper.readValue(responseRoom.getBody(), new TypeReference<>(){});

            List<RoomScheduleViewModel> roomScheduleViewModelList = new ArrayList<>();
            content = new LinkedMultiValueMap<>();
            content.add("date", date.toString());
            requestPOST = new HttpEntity<>(content, headers);
            HttpEntity<ResponseModel> responseScheduleDetails = restTemplate.exchange(
                    SCHEDULE_DETAIL_URL+"/findSchedulesOrNullByDate",
                    HttpMethod.POST ,requestPOST, ResponseModel.class);

            String jsonScheduleDetails = objectMapper.writeValueAsString(responseScheduleDetails.getBody().getData());
            scheduleDetailList = objectMapper.readValue(jsonScheduleDetails, new TypeReference<>() {});
            if (scheduleDetailList.size() == 0){
                return objectMapper.writeValueAsString(roomScheduleViewModelList);
            }

            var scheduleDetailListTemp = scheduleDetailList.stream().filter(
                    StreamHelper.distinctByKey(ScheduleDetail::getScheduleId)).toList();
            //get ScheduleList
            for (ScheduleDetail item: scheduleDetailListTemp) {
                HttpEntity<Schedule> responseSchedule = restTemplate.exchange(
                        SCHEDULE_URL + "findOne/" +item.getScheduleId(),
                        HttpMethod.GET, requestGET, Schedule.class);
                String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody());
                Schedule schedule = objectMapper.readValue(jsonSchedule, new TypeReference<>(){});
                schedule.setScheduleDetailsById(null);
                scheduleList.add(schedule);
            }

            var scheduleListTemp = scheduleList.stream()
                    .filter(StreamHelper.distinctByKey(Schedule::getClassId)).toList();
            for (Schedule item: scheduleListTemp){
                HttpEntity<ResponseModel> responseClass = restTemplate.exchange(
                        CLASS_URL + "getClass/" +item.getClassId(),
                        HttpMethod.GET, requestGET, ResponseModel.class);
                String jsonParse = objectMapper.writeValueAsString(responseClass.getBody().getData());
                Classses clazz = objectMapper.readValue(jsonParse, new TypeReference<>(){});
                clazz.setSchedulesById(null);
                classesList.add(clazz);
            }

            for (Schedule schedule: scheduleList){
                schedule.setScheduleDetailsById(scheduleDetailList.stream()
                                .filter(scheduleDetail -> scheduleDetail.getScheduleId() == schedule.getId())
                        .collect(Collectors.toCollection(ArrayList::new)));
            }
            for (Classses clazz : classesList){
                List<Schedule> list = new ArrayList<>();
                for (Schedule schedule: scheduleList){
                    if (schedule.getClassId().equals(clazz.getId())){
                        list.add(schedule);
                    }
                    clazz.setSchedulesById(list);
                }
            }
            for (Room room : roomList){
                List<Classses> list = new ArrayList<>();
                for (Classses clazz: classesList){
                    if (clazz.getRoomId().equals(room.getId())){
                        list.add(clazz);
                    }
                    room.setRoomClass(list);
                }
            }

            return objectMapper.writeValueAsString(roomList);


        }catch (Exception e){
            throw new ErrorHandler(e.getMessage());
        }
    }
}
