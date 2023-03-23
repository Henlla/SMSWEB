package com.example.smsweb.client.student;

import com.example.smsweb.api.exception.ErrorHandler;
import com.example.smsweb.dto.*;
import com.example.smsweb.dto.teacher.InputMarkModel;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Controller
@RequestMapping("student")
public class StudentClientController {
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String ACCOUNT_URL = "http://localhost:8080/api/accounts/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String SUBJECT_URL = "http://localhost:8080/api/subject/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";
    private final String NEWS_URL = "http://localhost:8080/api/news/";

    @GetMapping("/index")
    public String index(@CookieValue(name = "_token", defaultValue = "") String _token, Model model, Authentication auth) throws JsonProcessingException {
        String isExpired = JWTUtils.isExpired(_token);
        if (!isExpired.toLowerCase().equals("token expired")) {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            ResponseEntity<ResponseModel> response = restTemplate.getForEntity(NEWS_URL + "list", ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            List<News> newsList = new ObjectMapper().readValue(json, new TypeReference<List<News>>() {
            });
            Account studentAccount = (Account) auth.getPrincipal();
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Profile> profileResponse = restTemplate
                    .exchange(PROFILE_URL + "get/" + studentAccount.getId(), HttpMethod.GET, request, Profile.class);

            ResponseEntity<Student> studentResponse = restTemplate.exchange(
                    STUDENT_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET, request,
                    Student.class);

            ResponseEntity<ResponseModel> studentClassResponse = restTemplate.exchange(
                    STUDENT_CLASS_URL + "getStudent/" + studentResponse.getBody().getId(), HttpMethod.GET, request,
                    ResponseModel.class);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            String jsonStudentClass = objectMapper.writeValueAsString(studentClassResponse.getBody().getData());
            List<StudentClass> studentClassList = objectMapper.readValue(jsonStudentClass,
                    new TypeReference<List<StudentClass>>() {
                    });
            List<Classses> listClass = new ArrayList<>();

            for (StudentClass studentClass : studentClassList) {
                ResponseEntity<ResponseModel> classResponse = restTemplate.exchange(
                        CLASS_URL + "getClass/" + studentClass.getClassId(), HttpMethod.GET, request,
                        ResponseModel.class);
                String jsonClass = objectMapper.writeValueAsString(classResponse.getBody().getData());
                Classses classses = objectMapper.readValue(jsonClass, Classses.class);
                listClass.add(classses);
            }

            LocalDate now = LocalDate.now();
            List<TeachingCurrenDate> lCurrenDates = new ArrayList<>();
            for(Classses classses: listClass){
                for (Schedule schedule : classses.getSchedulesById()){
                    for (ScheduleDetail scheduleDetail:schedule.getScheduleDetailsById()){
                        if(LocalDate.parse(scheduleDetail.getDate()).equals(now)){
                            TeachingCurrenDate currentDateTeaching = new TeachingCurrenDate();
                            if (classses.getShift().substring(0, 1).equals("M")) {
                                if (scheduleDetail.getSlot().equals(1)) {
                                    currentDateTeaching.setClassCode(classses.getClassCode());
                                    currentDateTeaching.setDate(scheduleDetail.getDate());
                                    currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                    currentDateTeaching.setTime("7:30 - 9:30");
                                    currentDateTeaching.setRoomCode(classses.getClassRoom().getRoomCode());
                                    currentDateTeaching.setStartTime("7:30");
                                } else {
                                    currentDateTeaching.setClassCode(classses.getClassCode());
                                    currentDateTeaching.setDate(scheduleDetail.getDate());
                                    currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                    currentDateTeaching.setTime("9:30 - 11:30");
                                    currentDateTeaching.setRoomCode(classses.getClassRoom().getRoomCode());
                                    currentDateTeaching.setStartTime("9:30");

                                }
                            } else if (classses.getShift().substring(0, 1).equals("A")) {
                                if (scheduleDetail.getSlot().equals(1)) {
                                    currentDateTeaching.setClassCode(classses.getClassCode());
                                    currentDateTeaching.setDate(scheduleDetail.getDate());
                                    currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                    currentDateTeaching.setTime("12:30 - 15:30");
                                    currentDateTeaching.setRoomCode(classses.getClassRoom().getRoomCode());
                                    currentDateTeaching.setStartTime("12:30");
                                } else {
                                    currentDateTeaching.setClassCode(classses.getClassCode());
                                    currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()).toString());
                                    currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                    currentDateTeaching.setRoomCode(classses.getClassRoom().getRoomCode());
                                    currentDateTeaching.setTime("15:30 - 17:30");
                                    currentDateTeaching.setStartTime("15:30");
                                }
                            } else {
                                if (scheduleDetail.getSlot().equals(1)) {
                                    currentDateTeaching.setClassCode(classses.getClassCode());
                                    currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()).toString());
                                    currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                    currentDateTeaching.setRoomCode(classses.getClassRoom().getRoomCode());
                                    currentDateTeaching.setTime("17:30 - 19:30");
                                    currentDateTeaching.setStartTime("17:30");
                                } else {
                                    currentDateTeaching.setClassCode(classses.getClassCode());
                                    currentDateTeaching.setDate(LocalDate.parse(scheduleDetail.getDate()).toString());
                                    currentDateTeaching.setSubject(scheduleDetail.getSubjectBySubjectId());
                                    currentDateTeaching.setTime("19:30 - 21:30");
                                    currentDateTeaching.setRoomCode(classses.getClassRoom().getRoomCode());
                                    currentDateTeaching.setStartTime("19:30");
                                }
                            }
                            lCurrenDates.add(currentDateTeaching);
                        }
                    }
                }
            }

            model.addAttribute("listNews", newsList
                    .stream()
                    .filter(news -> news.getIsActive().equals(true))
                    .map(news -> {
                        news.setPost_date(LocalDate.parse(news.getPost_date()).format(dateTimeFormatter).toString());
                        return news;
                    })
                    .toList());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL");
            LocalDate currentDate = LocalDate.now();
            model.addAttribute("student",studentResponse.getBody());
            model.addAttribute("classList",listClass);
            model.addAttribute("currentStudy",lCurrenDates);
            model.addAttribute("currentDate", currentDate.format(formatter));
            model.addAttribute("major",studentResponse.getBody().getMajorStudentsById().get(0));
            return "student/index";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/viewSchedule")
    public String viewSchedule(@CookieValue(name = "_token", defaultValue = "") String _token, Model model,
                               Authentication auth) throws JsonProcessingException {
        String isExpired = JWTUtils.isExpired(_token);
        if (!isExpired.toLowerCase().equals("token expired")) {
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            Account studentAccount = (Account) auth.getPrincipal();
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Profile> profileResponse = restTemplate
                    .exchange(PROFILE_URL + "get/" + studentAccount.getId(), HttpMethod.GET, request, Profile.class);

            ResponseEntity<Student> studentResponse = restTemplate.exchange(
                    STUDENT_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET, request,
                    Student.class);

            ResponseEntity<ResponseModel> studentClassResponse = restTemplate.exchange(
                    STUDENT_CLASS_URL + "getStudent/" + studentResponse.getBody().getId(), HttpMethod.GET, request,
                    ResponseModel.class);
            String jsonStudentClass = objectMapper.writeValueAsString(studentClassResponse.getBody().getData());
            List<StudentClass> studentClassList = objectMapper.readValue(jsonStudentClass,
                    new TypeReference<List<StudentClass>>() {
                    });
            List<Classses> listClass = new ArrayList<>();

            for (StudentClass studentClass : studentClassList) {
                ResponseEntity<ResponseModel> classResponse = restTemplate.exchange(
                        CLASS_URL + "getClass/" + studentClass.getClassId(), HttpMethod.GET, request,
                        ResponseModel.class);
                String jsonClass = objectMapper.writeValueAsString(classResponse.getBody().getData());
                Classses classses = objectMapper.readValue(jsonClass, Classses.class);
                listClass.add(classses);
            }
            model.addAttribute("listClass", listClass);
            return "student/viewSchedule";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/viewSchedule/{classCode}")
    public String viewSchedule(@CookieValue(name = "_token", defaultValue = "") String _token,
                               Model model,
                               @PathVariable("classCode") String classCode) throws JsonProcessingException {
        String isExpired = JWTUtils.isExpired(_token);
        if (!isExpired.toLowerCase().equals("token expired")) {
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> paramsClass = new LinkedMultiValueMap<>();
            paramsClass.add("classCode", classCode);
            HttpEntity<MultiValueMap<String, Object>> requestClass = new HttpEntity<>(paramsClass, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST,
                    requestClass, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            Classses classses = objectMapper.readValue(json, Classses.class);

            LocalDate now = LocalDate.now(); // 2015-11-23
            LocalDate firstDay = now.with(firstDayOfYear()); // 2015-01-01
            LocalDate lastDay = now.with(lastDayOfYear()); // 2015-12-31
            int weekOfFirstDay = firstDay.get(WeekFields.of(Locale.getDefault()).weekOfYear());
            int weekOfLastDay = lastDay.get(WeekFields.of(Locale.getDefault()).weekOfYear());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM");
            List<WeekOfYear> weekOfYearList = new ArrayList<>();
            for (int i = weekOfFirstDay; i <= weekOfLastDay; i++) {
                StringBuilder toDate = new StringBuilder();
                for (LocalDate date = firstDay; date.isBefore(lastDay.plusDays(1)); date = date.plusDays(1)) {
                    int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfYear());
                    if (i == weekOfYear) {
                        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                        LocalDate startOfCurrentWeek = date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
                        DayOfWeek lastDayOfWeek = firstDayOfWeek.plus(6);
                        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
                        if (date.equals(startOfCurrentWeek)) {
                            toDate.append(date.format(dateTimeFormatter)).append(" To ");
                        } else if (date.equals(endOfWeek)) {
                            toDate.append(date.format(dateTimeFormatter));
                            WeekOfYear week = new WeekOfYear();
                            week.setDate(toDate.toString());
                            week.setWeek(weekOfYear);
                            weekOfYearList.add(week);
                        }
                    } else {
                        firstDay = date;
                        break;
                    }
                }
            }
            model.addAttribute("weekList", weekOfYearList);
            model.addAttribute("classses", classses);
            return "student/viewScheduleDetails";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/getScheduleDetails")
    @ResponseBody
    public Object getScheduleDetails(@CookieValue(name = "_token", defaultValue = "") String _token,
                                     @RequestParam("classId") Integer classId,
                                     @RequestParam("semester") String semester) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classId", classId);
            params.add("semester", Integer.parseInt(semester));
            HttpEntity<MultiValueMap<String, Object>> requestSchedule = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(
                    SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestSchedule,
                    ResponseModel.class);
            if (responseSchedule.getStatusCode().is2xxSuccessful()) {
                String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
                if(schedule==null){
                    return null;
                }
                ScheduleModel scheduleModel = new ScheduleModel();
                List<DayInWeek> dayInWeekList = new ArrayList<>();
                boolean flag = false;
                LocalDate minDate = schedule.getScheduleDetailsById().stream()
                        .map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).min(LocalDate::compareTo)
                        .get();
                LocalDate maxDate = schedule.getScheduleDetailsById().stream()
                        .map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).max(LocalDate::compareTo)
                        .get();
                List<ScheduleDetail> listSortDate = schedule.getScheduleDetailsById().stream()
                        .sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate())))
                        .toList();
                int index = 0;
                for (LocalDate date = minDate; date.isBefore(maxDate.plusDays(1)); date = date.plusDays(1)) {
                    if (listSortDate.size() == index) {
                        break;
                    }
                    for (int i = index; i < listSortDate.size(); i++) {
                        WeekFields weekFields = WeekFields.of(Locale.getDefault());
                        int weekOfMonth = LocalDate.parse(listSortDate.get(i).getDate()).get(weekFields.weekOfMonth());
                        int monthOfYear = LocalDate.parse(listSortDate.get(i).getDate()).getMonthValue();
                        int weekOfYear = LocalDate.parse(listSortDate.get(i).getDate())
                                .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                        if (date.isEqual(LocalDate.parse(listSortDate.get(i).getDate()))) {
                            DayInWeek diw = new DayInWeek();
                            if (listSortDate.get(listSortDate.size() - 1).equals(listSortDate.get(i)) && date
                                    .equals(LocalDate.parse(listSortDate.get(listSortDate.size() - 1).getDate()))) {
                                diw = new DayInWeek();
                                diw.setId(listSortDate.get(i).getId());
                                diw.setDate(LocalDate.parse(listSortDate.get(i).getDate()));
                                diw.setSubject(listSortDate.get(i).getSubjectBySubjectId());
                                diw.setDayOfWeek(listSortDate.get(i).getDayOfWeek());
                                diw.setSubjectId(listSortDate.get(i).getSubjectId());
                                diw.setTeacher(listSortDate.get(i).getTeacherByScheduleDetail());
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
                            diw.setTeacher(listSortDate.get(i).getTeacherByScheduleDetail());
                            diw.setWeek(weekOfMonth);
                            diw.setMonth(monthOfYear);
                            diw.setWeekOfYear(weekOfYear);
                            dayInWeekList.add(diw);
                            index = i;
                            if (LocalDate
                                    .parse(listSortDate.get(listSortDate.indexOf(listSortDate.get(i)) + 1).getDate())
                                    .isAfter(LocalDate.parse(listSortDate.get(i).getDate()))) {
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
                List<DayInWeek> listSort = scheduleModel.getDayInWeeks().stream()
                        .sorted((a, b) -> a.getWeekOfYear().compareTo(b.getWeekOfYear())).toList();
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
                        .collect(Collectors.toMap(HashMap.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                LinkedHashMap::new));
                return newMapSortedByKey;
            } else {
                return null;
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }

    @PostMapping("/viewScheduleByWeek")
    @ResponseBody
    public Object viewScheduleByWeek(@CookieValue(name = "_token", defaultValue = "") String _token,
                                     @RequestParam("classId") Integer classId,
                                     @RequestParam("semester") String semester,
                                     @RequestParam("week") Integer week) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("classId", classId);
            params.add("semester", Integer.parseInt(semester));
            HttpEntity<MultiValueMap<String, Object>> requestSchedule = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(
                    SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestSchedule,
                    ResponseModel.class);
            if (responseSchedule.getStatusCode().is2xxSuccessful()) {
                String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
                ScheduleModel scheduleModel = new ScheduleModel();
                List<DayInWeek> dayInWeekList = new ArrayList<>();
                boolean flag = false;
                LocalDate minDate = schedule.getScheduleDetailsById().stream()
                        .map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).min(LocalDate::compareTo)
                        .get();
                LocalDate maxDate = schedule.getScheduleDetailsById().stream()
                        .map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).max(LocalDate::compareTo)
                        .get();
                List<ScheduleDetail> listSortDate = schedule.getScheduleDetailsById().stream()
                        .sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate())))
                        .toList();
                int index = 0;
                for (LocalDate date = minDate; date.isBefore(maxDate.plusDays(1)); date = date.plusDays(1)) {
                    if (listSortDate.size() == index) {
                        break;
                    }
                    for (int i = index; i < listSortDate.size(); i++) {
                        WeekFields weekFields = WeekFields.of(Locale.getDefault());
                        int weekOfMonth = LocalDate.parse(listSortDate.get(i).getDate()).get(weekFields.weekOfMonth());
                        int monthOfYear = LocalDate.parse(listSortDate.get(i).getDate()).getMonthValue();
                        int weekOfYear = LocalDate.parse(listSortDate.get(i).getDate())
                                .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                        if (date.isEqual(LocalDate.parse(listSortDate.get(i).getDate()))) {
                            DayInWeek diw = new DayInWeek();
                            if (listSortDate.get(listSortDate.size() - 1).equals(listSortDate.get(i)) && date
                                    .equals(LocalDate.parse(listSortDate.get(listSortDate.size() - 1).getDate()))) {
                                diw = new DayInWeek();
                                diw.setId(listSortDate.get(i).getId());
                                diw.setDate(LocalDate.parse(listSortDate.get(i).getDate()));
                                diw.setSubject(listSortDate.get(i).getSubjectBySubjectId());
                                diw.setDayOfWeek(listSortDate.get(i).getDayOfWeek());
                                diw.setSubjectId(listSortDate.get(i).getSubjectId());
                                diw.setTeacher(listSortDate.get(i).getTeacherByScheduleDetail());
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
                            diw.setTeacher(listSortDate.get(i).getTeacherByScheduleDetail());
                            diw.setSlot(listSortDate.get(i).getSlot());
                            diw.setWeek(weekOfMonth);
                            diw.setMonth(monthOfYear);
                            diw.setWeekOfYear(weekOfYear);
                            dayInWeekList.add(diw);
                            index = i;
                            if (LocalDate
                                    .parse(listSortDate.get(listSortDate.indexOf(listSortDate.get(i)) + 1).getDate())
                                    .isAfter(LocalDate.parse(listSortDate.get(i).getDate()))) {
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
                List<DayInWeek> listSort = scheduleModel.getDayInWeeks().stream()
                        .sorted((a, b) -> a.getWeekOfYear().compareTo(b.getWeekOfYear())).toList();
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
                        .collect(Collectors.toMap(HashMap.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                LinkedHashMap::new));

                List<DayInWeek> getWeekKey = newMapSortedByKey.get(week);
                if (getWeekKey == null) {
                    return null;
                } else {
                    return getWeekKey;
                }
            } else {
                return null;
            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return null;
            }
        }
    }

    @GetMapping("/profile")
    public String profile(@CookieValue(name = "_token", defaultValue = "") String _token, Model model,
                          Authentication auth) throws JsonProcessingException {
        String isExpired = JWTUtils.isExpired(_token);
        if (!isExpired.toLowerCase().equals("token expired")) {
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            Account studentAccount = (Account) auth.getPrincipal();
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Profile> profileResponse = restTemplate
                    .exchange(PROFILE_URL + "get/" + studentAccount.getId(), HttpMethod.GET, request, Profile.class);

            ResponseEntity<Student> studentResponse = restTemplate.exchange(
                    STUDENT_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET, request,
                    Student.class);

            ResponseEntity<ResponseModel> studentClassResponse = restTemplate.exchange(
                    STUDENT_CLASS_URL + "getStudent/" + studentResponse.getBody().getId(), HttpMethod.GET, request,
                    ResponseModel.class);
            String jsonStudentClass = objectMapper.writeValueAsString(studentClassResponse.getBody().getData());
            List<StudentClass> studentClassList = objectMapper.readValue(jsonStudentClass,
                    new TypeReference<List<StudentClass>>() {
                    });
            List<Classses> listClass = new ArrayList<>();

            for (StudentClass studentClass : studentClassList) {
                ResponseEntity<ResponseModel> classResponse = restTemplate.exchange(
                        CLASS_URL + "getClass/" + studentClass.getClassId(), HttpMethod.GET, request,
                        ResponseModel.class);
                String jsonClass = objectMapper.writeValueAsString(classResponse.getBody().getData());
                Classses classses = objectMapper.readValue(jsonClass, Classses.class);
                listClass.add(classses);
            }
            model.addAttribute("listClass", listClass);
            model.addAttribute("account", studentAccount);
            model.addAttribute("student", studentResponse.getBody());
            return "student/profile";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/change_password/{accountId}")
    @ResponseBody
    public Object change_password(@CookieValue(name = "_token", defaultValue = "") String _token,
                                  @RequestParam("oldPass") String oldPass,
                                  @RequestParam("newPass") String newPass,
                                  @PathVariable("accountId") Integer accountId) {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("password", oldPass);
            params.add("newPassword", newPass);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(ACCOUNT_URL + "changePassword/" + accountId, HttpMethod.PUT, request, ResponseModel.class);
            return "success";
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return "error";
            }
        }
    }

    @GetMapping("/newsDetails/{newId}")
    public String newsDetails(@CookieValue(name = "_token", defaultValue = "") String _token,
                              @PathVariable("newId") Integer newId, Model model) {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ResponseModel> response = restTemplate.getForEntity(NEWS_URL + "get/" + newId, ResponseModel.class);
            model.addAttribute("news", response.getBody().getData());
            return "student/newDetails";
        } catch (Exception ex) {
            return "redirect:/login";
        }

    }


    @GetMapping("/marks")
    public String viewMarks(@CookieValue("_token")String _token){
        try{
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";
            return "student/marks";
        }catch (Exception e){
            return "redirect:/login";
        }
    }
    @GetMapping("/get-marks-list")
    @ResponseBody
    public String getMarksList(@CookieValue(name = "_token", defaultValue = "")String _token,
                           Authentication auth){
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            Account studentAccount = (Account) auth.getPrincipal();
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Profile> profileResponse = restTemplate
                    .exchange(PROFILE_URL + "get/" + studentAccount.getId(), HttpMethod.GET, request, Profile.class);

            ResponseEntity<Student> studentResponse = restTemplate.exchange(
                    STUDENT_URL + "getByProfile/" + profileResponse.getBody().getId(), HttpMethod.GET, request,
                    Student.class);
            Student student = studentResponse.getBody();

            //Get Subject import to InputMarkModel(student, subject)
            List<InputMarkModel> listStudentSubject = new ArrayList<>();

            for (StudentSubject item : student.getStudentSubjectsById()) {
                //Get Subject by subjectId
                request = new HttpEntity<>(headers);
                ResponseEntity<String> responseSubject = restTemplate.exchange(SUBJECT_URL + "getSubjectBySubjectId/" + item.getSubjectId(),
                        HttpMethod.GET, request, String.class);
                ResponseModel responseModelSubject = new ObjectMapper().readValue(responseSubject.getBody(), new TypeReference<ResponseModel>() {});
                String jsonSubject = new ObjectMapper().writeValueAsString(responseModelSubject.getData());
                Subject subject = new ObjectMapper().readValue(jsonSubject, Subject.class);

                InputMarkModel inputMarkModel = new InputMarkModel(student, subject);
                Mark itemMark = item.getMarksById().stream().filter(mark -> mark.getStudentSubjectId() == item.getId()).findFirst().orElse(null);
                inputMarkModel.setAsmMark(itemMark.getAsm());
                inputMarkModel.setObjMark(itemMark.getObj());

                listStudentSubject.add(inputMarkModel);
            }
            String jsonMarks = objectMapper.writeValueAsString(listStudentSubject);
            return jsonMarks;
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }
}
