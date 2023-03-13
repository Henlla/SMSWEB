package com.example.smsweb.client.dashboard;


import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.dto.*;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.mail.Mail;
import com.example.smsweb.mail.MailService;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.DayOfWeekSchedule;
import com.example.smsweb.utils.ExcelExport.ClassExport;
import com.example.smsweb.utils.ExcelExport.ScheduleExport;
import com.example.smsweb.utils.ExcelHelper;
import com.example.smsweb.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/dashboard/class")
@MultipartConfig
public class ClassController {

    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    private final String PROVINCE_URL = "http://localhost:8080/api/provinces/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";
    private final String HOLIDAY_URL = "https://holidayapi.com/v1/holidays?pretty&key=97662a7f-e120-4e95-b3a8-7c18d1c40717&country=VN&year=2022";
    @Autowired
    private MailService mailService;

    @GetMapping("/class-index")
    public String index(Model model, @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "get", HttpMethod.GET, request, String.class);
        List<Classses> classList = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<Classses>>() {
        });
        model.addAttribute("classes", classList);
        return "dashboard/class/class_index";
    }


    @GetMapping("/class-create")
    public String createClass(Model model, @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> teacherResponse = restTemplate.exchange(TEACHER_URL + "list", HttpMethod.GET, request, String.class);
        List<Teacher> teacherList = new ObjectMapper().readValue(teacherResponse.getBody(), new TypeReference<List<Teacher>>() {
        });

        ResponseModel listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);

        model.addAttribute("majors", listMajor.getData());
        model.addAttribute("teachers", teacherList);
        return "dashboard/class/class_create";
    }

    @GetMapping("/class-details/{classCode}")
    public String class_details(Model model, @CookieValue(name = "_token", defaultValue = "") String _token,
                                @PathVariable("classCode") String classCode) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classCode", classCode);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST, request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            Classses classses = new ObjectMapper().readValue(json, Classses.class);
            HttpEntity<Object> request2 = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response2 = restTemplate.exchange(CLASS_URL + "findClassByMajorId/" + classses.getMajorId(), HttpMethod.GET, request2, ResponseModel.class);
            String json2 = new ObjectMapper().writeValueAsString(response2.getBody().getData());
            List<Classses> listClass = new ObjectMapper().readValue(json2, new TypeReference<List<Classses>>() {
            });
            model.addAttribute("class", classses);
            model.addAttribute("classList", listClass.stream().filter(classses1 -> !classses1.getClassCode().equals(classses.getClassCode())).collect(Collectors.toList()));
            return "dashboard/class/class_details";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return "redirect:/dashboard/logout";
        }
    }

    @GetMapping("/class-update/{id}")
    @ResponseBody
    public Object class_update(@CookieValue(name = "_token", defaultValue = "") String _token,
                               @PathVariable("id") Integer id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "findOne/" + id,
                HttpMethod.GET, request, String.class);
        ResponseModel responseModel = objectMapper.readValue(response.getBody(), new TypeReference<ResponseModel>() {
        });
        String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
        Classses classModel = objectMapper.readValue(convertToJson, Classses.class);
        return classModel;
    }

    @PostMapping("/class-checkExisted")
    @ResponseBody
    public String checkExistedClass(@CookieValue(name = "_token", defaultValue = "") String _token,
                                    @RequestParam("classCode") String classCode) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);

            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            content.add("classCode", classCode);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
            ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST, request, String.class);

            return response.toString();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return e.getMessage();
            } else return e.getMessage();
        }
    }


    @GetMapping("/class-searchClasssesByClassCode")
    @ResponseBody
    public String searchClasssesByClassCode(Model model, @CookieValue(name = "_token", defaultValue = "") String _token,
                                            @RequestParam("classCode") String classCode) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "searchClasssesByClassCode/" + classCode, HttpMethod.GET, request, String.class);
        return response.getBody();
    }

    @PostMapping("/create_schedule")
    @ResponseBody
    public String create_schedule(@CookieValue(name = "_token", defaultValue = "") String _token,
                                  @RequestParam("startDate") String startDate,
                                  @RequestParam("semester") Integer semester,
                                  @RequestParam("majorId") Integer majorId,
                                  @RequestParam("shift") String shift,
                                  @RequestParam("classId") String classId) {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            Schedule scheduleParams = new Schedule();
            scheduleParams.setEndDate("");
            scheduleParams.setStartDate(startDate);
            scheduleParams.setClassId(Integer.parseInt(classId));
            scheduleParams.setSemester(semester);
            String json = objectMapper.writeValueAsString(scheduleParams);
            params.add("schedule", json);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "post", HttpMethod.POST, request, ResponseModel.class);
            String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
            Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);

            ResponseEntity<ResponseModel> responseMajor = restTemplate.getForEntity(MAJOR_URL + "get/" + majorId, ResponseModel.class);
            String jsonMajor = objectMapper.writeValueAsString(responseMajor.getBody().getData());
            Major major = objectMapper.readValue(jsonMajor, Major.class);

            List<Subject> listSubject = major.getSubjectsById().stream().filter(subject -> subject.getSemesterId() == semester).toList();

            LocalDate startDates = LocalDate.parse(startDate);
            LocalDate endDateTemp = LocalDate.parse(startDate).plusYears(1);

            ResponseEntity<HolidayModel> responseHoliday = restTemplate.getForEntity(HOLIDAY_URL, HolidayModel.class);
            HolidayModel holidayModel = responseHoliday.getBody();
            List<Holiday> holidayList = holidayModel.getHolidays();
            LocalDate endDate;
            List<ScheduleDetail> listScheduleDetails = new ArrayList<>();
            char getShift = shift.charAt(1);
            int slot = 0;

            if (getShift == '0') {
                for (Subject subject : listSubject) {
                    slot = 0;
                    for (LocalDate date = startDates; date.isBefore(endDateTemp); date = date.plusDays(1)) {
                        for (int i = 1; i <= subject.getSlot(); i++) {
                            int getDate = date.getDayOfMonth();
                            int getMonth = date.getMonthValue();
                            String dayOfWeek = String.valueOf(date.getDayOfWeek());
                            boolean isInHoliday = holidayList.stream().noneMatch(holiday -> holiday.getDate().getMonthValue() == getMonth && holiday.getDate().getDayOfMonth() == getDate);
                            if (Arrays.stream(DayOfWeekSchedule.array0).anyMatch(dayOfWeek::equals)
                                    && isInHoliday) {
                                ScheduleDetail scheduleDetail = new ScheduleDetail();
                                if (i % 2 == 0) {
                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                    scheduleDetail.setDate(date.toString());
                                    scheduleDetail.setScheduleId(schedule.getId());
                                    scheduleDetail.setSubjectId(subject.getId());
                                    scheduleDetail.setSlot(i);
                                    listScheduleDetails.add(scheduleDetail);
                                    slot++;
                                    break;
                                } else {
                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                    scheduleDetail.setDate(date.toString());
                                    scheduleDetail.setScheduleId(schedule.getId());
                                    scheduleDetail.setSubjectId(subject.getId());
                                    scheduleDetail.setSlot(i);
                                    listScheduleDetails.add(scheduleDetail);
                                    slot++;
                                    if (subject.getSlot() == slot) {
                                        startDates = LocalDate.parse(listScheduleDetails.get(listScheduleDetails.size() - 1).getDate()).plusDays(1);
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                        if (subject.getSlot() == slot) {
                            startDates = LocalDate.parse(listScheduleDetails.get(listScheduleDetails.size() - 1).getDate()).plusDays(1);
                            break;
                        }
                    }
                    if (listSubject.get(listSubject.size() - 1).equals(subject) && listSubject.get(listSubject.size() - 1).getSlot() == slot) {
                        break;
                    }
                }
            } else {
                for (Subject subject : listSubject) {
                    slot = 0;
                    for (LocalDate date = startDates; date.isBefore(endDateTemp); date = date.plusDays(1)) {
                        for (int i = 1; i <= subject.getSlot(); i++) {
                            int getDate = date.getDayOfMonth();
                            int getMonth = date.getMonthValue();
                            String dayOfWeek = String.valueOf(date.getDayOfWeek());
                            boolean isInHoliday = holidayList.stream().noneMatch(holiday -> holiday.getDate().getMonthValue() == getMonth && holiday.getDate().getDayOfMonth() == getDate);
                            if (Arrays.stream(DayOfWeekSchedule.array1).anyMatch(dayOfWeek::equals)
                                    && isInHoliday) {
                                ScheduleDetail scheduleDetail = new ScheduleDetail();
                                if (i % 2 == 0) {
                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                    scheduleDetail.setDate(date.toString());
                                    scheduleDetail.setScheduleId(schedule.getId());
                                    scheduleDetail.setSubjectId(subject.getId());
                                    scheduleDetail.setSlot(i);
                                    listScheduleDetails.add(scheduleDetail);
                                    slot++;
                                    break;
                                } else {
                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                    scheduleDetail.setDate(date.toString());
                                    scheduleDetail.setScheduleId(schedule.getId());
                                    scheduleDetail.setSubjectId(subject.getId());
                                    scheduleDetail.setSlot(i);
                                    listScheduleDetails.add(scheduleDetail);
                                    slot++;
                                    if (subject.getSlot() == slot) {
                                        startDates = LocalDate.parse(listScheduleDetails.get(listScheduleDetails.size() - 1).getDate()).plusDays(1);
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                        if (subject.getSlot() == slot) {
                            startDates = LocalDate.parse(listScheduleDetails.get(listScheduleDetails.size() - 1).getDate()).plusDays(1);
                            break;
                        }
                    }
                    if (listSubject.get(listSubject.size() - 1).equals(subject) && listSubject.get(listSubject.size() - 1).getSlot() == slot) {
                        break;
                    }
                }
            }
            MultiValueMap<String, Object> paramsScheduleDetails = new LinkedMultiValueMap<>();
            paramsScheduleDetails.add("listSchedule", objectMapper.writeValueAsBytes(listScheduleDetails));
            HttpEntity<MultiValueMap<String, Object>> requestScheduleDetails = new HttpEntity<>(paramsScheduleDetails, headers);
            ResponseEntity<String> responseScheduleDetails = restTemplate.exchange(SCHEDULE_DETAIL_URL + "saveAll", HttpMethod.POST, requestScheduleDetails, String.class);
            schedule.setEndDate(listScheduleDetails.get(listScheduleDetails.size() - 1).getDate().toString());
            MultiValueMap<String, Object> paramsSchedule = new LinkedMultiValueMap<>();
            paramsSchedule.add("schedule", objectMapper.writeValueAsBytes(schedule));
            HttpEntity<MultiValueMap<String, Object>> requestSchedule = new HttpEntity<>(paramsSchedule, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(SCHEDULE_URL + "put", HttpMethod.PUT, requestSchedule, ResponseModel.class);
            return "success";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/getScheduleDetails")
    @ResponseBody
    public Object getScheduleDetails(@CookieValue(name = "_token", defaultValue = "") String _token,
                                     @RequestParam("classId") String classId,
                                     @RequestParam("semester") String semester) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classId", Integer.parseInt(classId));
            params.add("semester", Integer.parseInt(semester));
            HttpEntity<MultiValueMap<String, Object>> requestSchedule = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestSchedule, ResponseModel.class);
            if (responseSchedule.getStatusCode().is2xxSuccessful()) {
                String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
                ScheduleModel scheduleModel = new ScheduleModel();
                List<DayInWeek> dayInWeekList = new ArrayList<>();
                boolean flag = false;
                LocalDate minDate = schedule.getScheduleDetailsById().stream().map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).min(LocalDate::compareTo).get();
                LocalDate maxDate = schedule.getScheduleDetailsById().stream().map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).max(LocalDate::compareTo).get();
                List<ScheduleDetail> listSortDate = schedule.getScheduleDetailsById().stream().sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate()))).toList();
                int index = 0;
                for (LocalDate date = minDate; date.isBefore(maxDate.plusDays(1)); date = date.plusDays(1)) {
                    if (listSortDate.size() == index) {
                        break;
                    }
                    for (int i = index; i < listSortDate.size(); i++) {
                        WeekFields weekFields = WeekFields.of(Locale.getDefault());
                        int weekOfMonth = LocalDate.parse(listSortDate.get(i).getDate()).get(weekFields.weekOfMonth());
                        int monthOfYear = LocalDate.parse(listSortDate.get(i).getDate()).getMonthValue();
                        int weekOfYear = LocalDate.parse(listSortDate.get(i).getDate()).get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                        if (date.isEqual(LocalDate.parse(listSortDate.get(i).getDate()))) {
                            DayInWeek diw = new DayInWeek();
                            if (listSortDate.get(listSortDate.size() - 1).equals(listSortDate.get(i)) && date.equals(LocalDate.parse(listSortDate.get(listSortDate.size() - 1).getDate()))) {
                                diw = new DayInWeek();
                                diw.setId(listSortDate.get(i).getId());
                                diw.setDate(LocalDate.parse(listSortDate.get(i).getDate()));
                                diw.setSubject(listSortDate.get(i).getSubjectBySubjectId());
                                diw.setDayOfWeek(listSortDate.get(i).getDayOfWeek());
                                diw.setSubjectId(listSortDate.get(i).getSubjectId());
                                diw.setWeek(weekOfMonth);
                                diw.setSlot(listSortDate.get(i).getSlot());
                                diw.setSubjectId(listSortDate.get(i).getSubjectId());
                                diw.setMonth(monthOfYear);
                                diw.setWeekOfYear(weekOfYear);
                                dayInWeekList.add(diw);
                                index = i;
                                break;
                            }
                            diw = new DayInWeek();
                            diw.setId(listSortDate.get(i).getId());
                            diw.setDate(LocalDate.parse(listSortDate.get(i).getDate()));
                            diw.setSubject(listSortDate.get(i).getSubjectBySubjectId());
                            diw.setDayOfWeek(listSortDate.get(i).getDayOfWeek());
                            diw.setSubjectId(listSortDate.get(i).getSubjectId());
                            diw.setSlot(listSortDate.get(i).getSlot());
                            diw.setWeek(weekOfMonth);
                            diw.setMonth(monthOfYear);
                            diw.setWeekOfYear(weekOfYear);
                            dayInWeekList.add(diw);
                            index = i;
                            if (LocalDate.parse(listSortDate.get(listSortDate.indexOf(listSortDate.get(i)) + 1).getDate()).isAfter(LocalDate.parse(listSortDate.get(i).getDate()))) {
                                index = listSortDate.indexOf(listSortDate.get(i)) + 1;
                                break;
                            }
                        } else {
                            DayInWeek diw = new DayInWeek();
                            diw.setId(0);
                            diw.setDate(date);
                            diw.setSubject(null);
                            diw.setDayOfWeek(String.valueOf(date.getDayOfWeek().getValue()));
                            diw.setSubjectId(0);
                            diw.setSlot(1);
                            diw.setWeek(date.get(weekFields.weekOfMonth()));
                            diw.setMonth(date.getMonthValue());
                            diw.setWeekOfYear(date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
                            dayInWeekList.add(diw);

                            diw = new DayInWeek();
                            diw.setId(0);
                            diw.setDate(date);
                            diw.setSubject(null);
                            diw.setDayOfWeek(String.valueOf(date.getDayOfWeek().getValue()));
                            diw.setSubjectId(0);
                            diw.setSlot(2);
                            diw.setWeek(date.get(weekFields.weekOfMonth()));
                            diw.setMonth(date.getMonthValue());
                            diw.setWeekOfYear(date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
                            dayInWeekList.add(diw);
                            break;
                        }

                    }


                }
                scheduleModel.setStartDate(LocalDate.parse(schedule.getStartDate()));
                scheduleModel.setId(schedule.getId());
                scheduleModel.setEndDate(LocalDate.parse(schedule.getEndDate()));
                scheduleModel.setSemester(schedule.getSemester());
                scheduleModel.setDayInWeeks(dayInWeekList);
                HashMap<Integer, List<DayInWeek>> hashMap = new HashMap<Integer, List<DayInWeek>>();
                List<DayInWeek> listSort = scheduleModel.getDayInWeeks().stream().sorted((a, b) -> a.getWeekOfYear().compareTo(b.getWeekOfYear())).toList();
                for (DayInWeek dayInWeek : listSort) {
                    Integer key = dayInWeek.getWeekOfYear();
                    if (hashMap.containsKey(key)) {
                        List<DayInWeek> list = hashMap.get(key);
                        list.add(dayInWeek);

                    } else {
                        List<DayInWeek> list = new ArrayList<DayInWeek>();
                        list.add(dayInWeek);
                        hashMap.put(key, list);
                    }
                }
                HashMap<Integer, List<DayInWeek>> newMapSortedByKey = hashMap.entrySet().stream()
                        .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                        .collect(Collectors.toMap(HashMap.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
                return newMapSortedByKey;
            } else {
                return null;
            }
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }


    @PostMapping("/changeDateSchedule")
    @ResponseBody
    public Object changeDateSchedule(@CookieValue(name = "_token", defaultValue = "") String _token,
                                     @RequestParam("currenDate") String currenDate,
                                     @RequestParam("slot") Integer slot,
                                     @RequestParam("classId") String classId,
                                     @RequestParam("semester") String semester) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classId", Integer.parseInt(classId));
            params.add("semester", Integer.parseInt(semester));
            HttpEntity<MultiValueMap<String, Object>> requestSchedule = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestSchedule, ResponseModel.class);
            if (responseSchedule.getStatusCode().is2xxSuccessful()) {
                String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
                String[] splitDate = currenDate.split("/");
                String formatDate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
                LocalDate date = LocalDate.parse(formatDate);
                int getDate = date.getDayOfMonth();
                int getMonth = date.getMonthValue();
                boolean isSameDate = schedule.getScheduleDetailsById().stream()
                        .anyMatch(dates -> LocalDate.parse(dates.getDate()).getDayOfMonth() == getDate
                                && LocalDate.parse(dates.getDate()).getMonthValue() == getMonth && dates.getSlot().equals(slot));
                if (isSameDate) {
                    return "error";
                } else {
                    return "success";
                }
            }
            return "";
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }

    @PostMapping("/updateDateChangeSchedule")
    @ResponseBody
    public Object updateDateChangeSchedule(@CookieValue(name = "_token", defaultValue = "") String _token,
                                           @RequestParam("newDate") String newDate,
                                           @RequestParam("slot") Integer slot,
                                           @RequestParam("schedule_details_id") String schedule_details_id) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            String[] splitDate = newDate.split("/");
            String formatDate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
            HttpEntity<Object> requestScheduleDetails = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(SCHEDULE_DETAIL_URL + "get/" + Integer.parseInt(schedule_details_id), HttpMethod.GET, requestScheduleDetails, ResponseModel.class);
            String jsonScheduleDetails = objectMapper.writeValueAsString(response.getBody().getData());
            ScheduleDetail scheduleDetail = objectMapper.readValue(jsonScheduleDetails, ScheduleDetail.class);
            String dayOfWeek = String.valueOf(LocalDate.parse(formatDate).getDayOfWeek());
            scheduleDetail.setDate(formatDate);
            scheduleDetail.setDayOfWeek(dayOfWeek);
            scheduleDetail.setSlot(slot);
            String convertToJson = objectMapper.writeValueAsString(scheduleDetail);
            MultiValueMap<String, Object> paramsDetails = new LinkedMultiValueMap<>();
            paramsDetails.add("schedule_details", convertToJson);
            HttpEntity<MultiValueMap<String, Object>> requestScheduleDetailsPUT = new HttpEntity<>(paramsDetails, headers);
            ResponseEntity<ResponseModel> responseScheduleDetails = restTemplate.exchange(SCHEDULE_DETAIL_URL + "put", HttpMethod.PUT, requestScheduleDetailsPUT, ResponseModel.class);
            if (responseScheduleDetails.getStatusCode().is2xxSuccessful()) {
                return "success";
            } else {
                return "error";
            }

        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }

    @GetMapping("/get-student-by-card/{studentCard}")
    @ResponseBody
    public String findStudent(
            @CookieValue(name = "_token", defaultValue = "") String _token,
            @PathVariable("studentCard") String studentCard
    ) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL + "findStudentByStudentCard/" + studentCard, HttpMethod.GET, request, String.class);
            Student student = objectMapper.readValue(response.getBody(), new TypeReference<Student>() {
            });
            if (student != null) {
                String convertToJson = objectMapper.writeValueAsString(student);
                return convertToJson;
            } else {
                throw new ErrorHandler("Không tìm thấy sinh viên: " + studentCard);
            }
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return ex.getMessage();
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/add-student-to-class")
    @ResponseBody
    public String addStudentToClass(
            @CookieValue(name = "_token", defaultValue = "") String _token,
            @RequestParam("studentId") int studentId,
            @RequestParam("classId") int classId
    ) {
        try {
            JWTUtils.checkExpired(_token);

            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);

            StudentClass studentClass = new StudentClass();
            studentClass.setStudentId(studentId);
            studentClass.setClassId(classId);

            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
            content.add("newStudentClass", objectMapper.writeValueAsString(studentClass));
            request = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "save", HttpMethod.POST, request, ResponseModel.class);

            if (responseStudentClass.getStatusCode().is2xxSuccessful()) {
                String convertToJson = new ObjectMapper().writeValueAsString(responseStudentClass.getBody().getData());
                return convertToJson;
            } else {
                throw new ErrorHandler("Thêm sinh viên thất bại");
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/import-student-excel")
    @ResponseBody
    public String importStudentExcel(
            @CookieValue(name = "_token", defaultValue = "") String _token,
            @RequestParam("classCode") String classCode,
            @RequestParam("availablePlace") int availablePlace,
            @RequestParam(name = "studentList", required = false) MultipartFile file
    ) {
        try {
            JWTUtils.checkExpired(_token);
            if (file != null) {
                try {
                    ClassResponse response = importStudentClass(_token, file, classCode, availablePlace);
                    if (response.getStatus() == "success") {
                        return response.toString();
                    } else {
                        throw new ErrorHandler(response.getMessage());
                    }
                } catch (Exception e) {
                    String message = e.getMessage().substring(e.getMessage().indexOf("\"") + 1, e.getMessage().lastIndexOf("\""));
                    throw new ErrorHandler(message);
                }
            } else {
                throw new ErrorHandler("Không thể thêm danh sách trống");
            }
        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @PostMapping("/class-create")
    @ResponseBody
    public String createClass(Model model,
                              @CookieValue(name = "_token", defaultValue = "") String _token,
                              @RequestParam("newClass") String newClass,
                              @RequestParam(name = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);

            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            content.add("newClass", newClass);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "save", HttpMethod.POST, request, ResponseModel.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Classses classModel = objectMapper.readValue(newClass, Classses.class);
                Object data = response.getBody().getData();
                if (file != null) {
                    try {
                        ClassResponse result = importStudentClass(_token, file, classModel.getClassCode(), classModel.getLimitStudent());
                        if (result.getStatus() == "success") {
                            return result.toString();
                        } else {
                            return new ClassResponse("success", "Thêm danh sách sinh viên thất bại. " + result.getMessage()).toString();
                        }
                    } catch (Exception e) {
                        String message = StringUtils.substringBetween(e.getMessage(), "\"", "\"");
                        return new ClassResponse("success", "Thêm danh sách sinh viên thất bại. " + message).toString();
                    }
                } else {
                    return new ClassResponse("success", "").toString();
                }
            } else {
                return new ClassResponse("fail", "").toString();
            }
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return ex.getMessage();
            }
        }

    }

    private ClassResponse importStudentClass(String _token, MultipartFile file, String classCode, int availablePlace) throws JsonProcessingException {
        if (!file.isEmpty()) {
            try {
                // get list studentId form Excel file
                List<String> listStudentCard = new ArrayList<>();
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet sheet = workbook.getSheetAt(0);
                for (int rowIndex = 0; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 1); rowIndex++) {
                    XSSFRow row = sheet.getRow(rowIndex);
                    if (rowIndex == 0) {
                        continue;
                    }
                    String student_code = ExcelHelper.getValue(row.getCell(1)).toString();
                    if (!student_code.isEmpty() && listStudentCard.indexOf(student_code) == -1) {
                        listStudentCard.add(student_code);
                    }
                }

                if (!listStudentCard.isEmpty()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Bearer " + _token);

                    MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);

                    ResponseEntity<String> responseStudent = restTemplate.exchange(STUDENT_URL + "findStudentIdByRangeStudentCard/" + new ObjectMapper().writeValueAsString(listStudentCard),
                            HttpMethod.GET, request, String.class);
                    List<Student> listStudent = objectMapper.readValue(responseStudent.getBody(), new TypeReference<List<Student>>() {
                    });
                    if (listStudent.size() > availablePlace) {
                        return new ClassResponse("error", "Chỉ được thêm " + availablePlace + " vào lớp");
                    }

                    content.add("classCode", classCode);
                    request = new HttpEntity<>(content, headers);
                    ResponseEntity<String> responseClass = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST, request, String.class);
                    content.remove("classCode");
                    ResponseModel responseModel = objectMapper.readValue(responseClass.getBody(), new TypeReference<ResponseModel>() {
                    });
                    String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
                    Classses classModel = objectMapper.readValue(convertToJson, Classses.class);
                    List<StudentClass> studentClassList = new ArrayList<>();
                    for (Student student : listStudent) {
                        StudentClass studentClass = new StudentClass();
                        studentClass.setStudentId(student.getId());
                        studentClass.setClassId(classModel.getId());
                        studentClassList.add(studentClass);
                    }
                    content.add("listStudentClass", objectMapper.writeValueAsString(studentClassList));
                    request = new HttpEntity<>(content, headers);
                    ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "saveAll", HttpMethod.POST, request, ResponseModel.class);
                    if (responseStudentClass.getStatusCode().is2xxSuccessful()) {
                        return new ClassResponse("success", "Thêm thành công " + studentClassList.size() + " sinh viên");
                    } else {
                        return new ClassResponse("error", "Thêm danh sách sinh viên thất bại");
                    }
                } else {
                    return new ClassResponse("error", "Không thể thêm danh sách trống");
                }
            } catch (Exception e) {
                var s = e.getMessage();
                throw new RuntimeException(e.getMessage());
            }
        }
        return new ClassResponse("error", "Không thể thêm danh sách trống");
    }

    @GetMapping("/export-student-excel/{ClassId}")
    public void exportStudentList(HttpServletResponse response,
                                  @CookieValue(name = "_token", defaultValue = "") String _token,
                                  @PathVariable("ClassId") int classId
    ) {
        try {
            JWTUtils.checkExpired(_token);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseEntity<String> responseClass = restTemplate.exchange(CLASS_URL + "findOne/" + classId, HttpMethod.GET, request, String.class);
            ResponseModel responseModel = objectMapper.readValue(responseClass.getBody(), new TypeReference<ResponseModel>() {
            });
            String convertToJson = objectMapper.writeValueAsString(responseModel.getData());

            Classses classModel = objectMapper.readValue(convertToJson, Classses.class);

            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=students_" + classModel.getClassCode() + ".xlsx";
            response.setHeader(headerKey, headerValue);


            ClassExport classExport = new ClassExport(classModel);
            classExport.generateExcelFile(response);

        } catch (HttpClientErrorException e) {
            throw new ErrorHandler("token expired");
        } catch (JsonProcessingException e) {
            throw new ErrorHandler(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getTeacherByCard/{card}")
    @ResponseBody
    public Object getTeacherByCard(@CookieValue(name = "_token", defaultValue = "") String _token,
                                   @PathVariable("card") String card) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<Teacher> response = restTemplate.exchange(TEACHER_URL + "getByCard/" + card, HttpMethod.GET, request, Teacher.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                if (response.getBody() == null) {
                    return "null";
                } else {
                    return response.getBody();
                }
            } else {
                return "error";
            }
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }

    @PostMapping("change_teacher")
    @ResponseBody
    public Object change_teacher(@CookieValue(name = "_token", defaultValue = "") String _token,
                                 @RequestParam("classId") Integer classId, @RequestParam("teacherCard") String teacherCard) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + _token);
            ObjectMapper objectMapper = new ObjectMapper();
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "getClass/" + classId, HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            Classses classses = objectMapper.readValue(json, Classses.class);
            HttpEntity<Object> requestTeacher = new HttpEntity<>(headers);
            ResponseEntity<String> responseTeacher = restTemplate.exchange(TEACHER_URL + "getByCard/" + teacherCard, HttpMethod.GET, request, String.class);
            Teacher teacher = objectMapper.readValue(responseTeacher.getBody(), Teacher.class);
            classses.setTeacherId(teacher.getId());

            String jsonClass = objectMapper.writeValueAsString(classses);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("class", jsonClass);
            HttpEntity<MultiValueMap<String, Object>> requestUpdateClass = new HttpEntity<>(params, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(CLASS_URL + "updateClass", HttpMethod.PUT, requestUpdateClass, String.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }


    @GetMapping("export_schedule/{classId}&{semester}")
    public void export_schedule(@CookieValue(name = "_token", defaultValue = "") String _token,
                                @PathVariable("classId") Integer classId,
                                @PathVariable("semester") Integer semester, HttpServletResponse responses) throws IOException {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classId", classId);
            params.add("semester", semester);
            HttpEntity<MultiValueMap<String, Object>> requestSchedule = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestSchedule, ResponseModel.class);
            String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
            Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "getClass/" + classId, HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            Classses classses = objectMapper.readValue(json, Classses.class);

            ScheduleModel scheduleModel = new ScheduleModel();
            List<DayInWeek> dayInWeekList = new ArrayList<>();
            boolean flag = false;
            LocalDate minDate = schedule.getScheduleDetailsById().stream().map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).min(LocalDate::compareTo).get();
            LocalDate maxDate = schedule.getScheduleDetailsById().stream().map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).max(LocalDate::compareTo).get();
            List<ScheduleDetail> listSortDate = schedule.getScheduleDetailsById().stream().sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate()))).toList();
            int index = 0;
            for (LocalDate date = minDate; date.isBefore(maxDate.plusDays(1)); date = date.plusDays(1)) {
                if (listSortDate.size() == index) {
                    break;
                }
                for (int i = index; i < listSortDate.size(); i++) {
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int weekOfMonth = LocalDate.parse(listSortDate.get(i).getDate()).get(weekFields.weekOfMonth());
                    int monthOfYear = LocalDate.parse(listSortDate.get(i).getDate()).getMonthValue();
                    int weekOfYear = LocalDate.parse(listSortDate.get(i).getDate()).get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                    if (date.isEqual(LocalDate.parse(listSortDate.get(i).getDate()))) {
                        DayInWeek diw = new DayInWeek();
                        if (listSortDate.get(listSortDate.size() - 1).equals(listSortDate.get(i)) && date.equals(LocalDate.parse(listSortDate.get(listSortDate.size() - 1).getDate()))) {
                            diw = new DayInWeek();
                            diw.setId(listSortDate.get(i).getId());
                            diw.setDate(LocalDate.parse(listSortDate.get(i).getDate()));
                            diw.setSubject(listSortDate.get(i).getSubjectBySubjectId());
                            diw.setDayOfWeek(listSortDate.get(i).getDayOfWeek());
                            diw.setSubjectId(listSortDate.get(i).getSubjectId());
                            diw.setWeek(weekOfMonth);
                            diw.setSlot(listSortDate.get(i).getSlot());
                            diw.setSubjectId(listSortDate.get(i).getSubjectId());
                            diw.setMonth(monthOfYear);
                            diw.setWeekOfYear(weekOfYear);
                            dayInWeekList.add(diw);
                            index = i;
                            break;
                        }
                        diw = new DayInWeek();
                        diw.setId(listSortDate.get(i).getId());
                        diw.setDate(LocalDate.parse(listSortDate.get(i).getDate()));
                        diw.setSubject(listSortDate.get(i).getSubjectBySubjectId());
                        diw.setDayOfWeek(listSortDate.get(i).getDayOfWeek());
                        diw.setSubjectId(listSortDate.get(i).getSubjectId());
                        diw.setSlot(listSortDate.get(i).getSlot());
                        diw.setWeek(weekOfMonth);
                        diw.setMonth(monthOfYear);
                        diw.setWeekOfYear(weekOfYear);
                        dayInWeekList.add(diw);
                        index = i;
                        if (LocalDate.parse(listSortDate.get(listSortDate.indexOf(listSortDate.get(i)) + 1).getDate()).isAfter(LocalDate.parse(listSortDate.get(i).getDate()))) {
                            index = listSortDate.indexOf(listSortDate.get(i)) + 1;
                            break;
                        }
                    } else {
                        DayInWeek diw = new DayInWeek();
                        diw.setId(0);
                        diw.setDate(date);
                        diw.setSubject(null);
                        diw.setDayOfWeek(String.valueOf(date.getDayOfWeek().getValue()));
                        diw.setSubjectId(0);
                        diw.setSlot(1);
                        diw.setWeek(date.get(weekFields.weekOfMonth()));
                        diw.setMonth(date.getMonthValue());
                        diw.setWeekOfYear(date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
                        dayInWeekList.add(diw);

                        diw = new DayInWeek();
                        diw.setId(0);
                        diw.setDate(date);
                        diw.setSubject(null);
                        diw.setDayOfWeek(String.valueOf(date.getDayOfWeek().getValue()));
                        diw.setSubjectId(0);
                        diw.setSlot(2);
                        diw.setWeek(date.get(weekFields.weekOfMonth()));
                        diw.setMonth(date.getMonthValue());
                        diw.setWeekOfYear(date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
                        dayInWeekList.add(diw);
                        break;
                    }
                }
            }
            scheduleModel.setStartDate(LocalDate.parse(schedule.getStartDate()));
            scheduleModel.setId(schedule.getId());
            scheduleModel.setEndDate(LocalDate.parse(schedule.getEndDate()));
            scheduleModel.setSemester(schedule.getSemester());
            scheduleModel.setDayInWeeks(dayInWeekList);

            responses.setContentType("application/octet-stream");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDate = dateFormat.format(new Date());
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + classses.getClassCode() + ".xlsx"; // file *.xlsx
            responses.setHeader(headerKey, headerValue);
            ScheduleExport generateFeedbackExcel = new ScheduleExport(scheduleModel, classses);
            generateFeedbackExcel.generateExcelFile(responses);
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    @PostMapping("sendSchedule")
    @ResponseBody
    public Object sendSchedule(@CookieValue(name = "_token", defaultValue = "") String _token,
                               @RequestParam("classId") Integer classId,
                               @RequestParam("file") MultipartFile file) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "getClass/" + classId, HttpMethod.GET, request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            Classses classses = new ObjectMapper().readValue(json, Classses.class);
            File attachment = FileUtils.convertMultiPartToFile(file);
            for (StudentClass student : classses.getStudentClassById()) {
                //Send mail
                Mail mail = new Mail();
                mail.setToMail(student.getClassStudentByStudent().getStudentByProfile().getEmail());
                mail.setSubject("Account student HKT SYSTEM");
                String name = student.getClassStudentByStudent().getStudentByProfile().getFirstName() + " " + student.getClassStudentByStudent().getStudentByProfile().getLastName();
                Map<String, Object> props = new HashMap<>();
                props.put("fullname", name);
                mail.setProps(props);
                mailService.sendHtmlMessageSendSchedule(mail, attachment);
                //-----------------------------
            }
            return "success";
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }

    @PostMapping("checkStudentInClass")
    @ResponseBody
    public Object checkStudentInClass(@CookieValue(name = "_token", defaultValue = "") String _token,
                                      @RequestParam("classId") Integer classId,
                                      @RequestParam("studentId") Integer studentId) {

        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(STUDENT_CLASS_URL + "getStudent/" + studentId, HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            List<StudentClass> studentClassList = objectMapper.readValue(json, new TypeReference<List<StudentClass>>() {
            });
            boolean isExistsInClass = studentClassList.stream().anyMatch(studentClass -> studentClass.getClassId().equals(classId));
            if (isExistsInClass) {
                return "error";
            } else {
                return "success";
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }

    @PostMapping("changeClassForStudent")
    @ResponseBody
    public Object changeClassForStudent(@CookieValue(name = "_token", defaultValue = "") String _token,
                                        @RequestParam("studentId") Integer studentId,
                                        @RequestParam("classIdChange") Integer classIdChange,
                                        @RequestParam("classIdCurrent") Integer classIdCurrent) {

        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(STUDENT_CLASS_URL + "getStudent/" + studentId, HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            List<StudentClass> studentClassList = objectMapper.readValue(json, new TypeReference<List<StudentClass>>() {
            });
            StudentClass studentClass = studentClassList.stream().filter(studentClass1 -> studentClass1.getClassId().equals(classIdCurrent) && studentClass1.getStudentId().equals(studentId)).findFirst().get();
            studentClass.setClassId(classIdChange);
            String jsonRequest = objectMapper.writeValueAsString(studentClass);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("studentClass", jsonRequest);
            HttpEntity<MultiValueMap<String, Object>> request2 = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response2 = restTemplate.exchange(STUDENT_CLASS_URL + "put", HttpMethod.PUT, request2, ResponseModel.class);
            if(response2.getStatusCode().is2xxSuccessful()){
                return "success";
            }else{
                return "error";
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }
}
