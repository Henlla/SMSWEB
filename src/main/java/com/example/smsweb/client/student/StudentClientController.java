package com.example.smsweb.client.student;

import com.example.smsweb.dto.DayInWeek;
import com.example.smsweb.dto.ResponseModel;
import com.example.smsweb.dto.ScheduleModel;
import com.example.smsweb.dto.WeekOfYear;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";

    @GetMapping("/index")
    public String index() {
        return "student/index";
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
                    int weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
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

}
