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
import com.example.smsweb.utils.StreamHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.plexus.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/dashboard/class")
@MultipartConfig
public class ClassController {

    private final String ACCOUNT_URL = "http://localhost:8080/api/accounts/";
    private final String PROFILE_URL = "http://localhost:8080/api/profiles/";
    private final String ROLE_URL = "http://localhost:8080/api/roles/";
    private final String MARK_URL = "http://localhost:8080/api/mark/";

    private final String STUDENT_MAJOR_URL = "http://localhost:8080/api/student-major/";
    private final String MAJOR_URL = "http://localhost:8080/api/major/";
    private final String DEPARTMENT_URL = "http://localhost:8080/api/department/";
    private final String PROVINCE_URL = "http://localhost:8080/api/provinces/";
    private final String CLASS_URL = "http://localhost:8080/api/classes/";
    private final String STUDENT_URL = "http://localhost:8080/api/students/";
    private final String STUDENT_CLASS_URL = "http://localhost:8080/api/student-class/";
    private final String STUDENT_SUBJECT_URL = "http://localhost:8080/api/student-subject/";
    private final String TEACHER_URL = "http://localhost:8080/api/teachers/";
    private final String SCHEDULE_URL = "http://localhost:8080/api/schedules/";
    private final String URL_ROOM = "http://localhost:8080/api/room/";
    private final String SCHEDULE_DETAIL_URL = "http://localhost:8080/api/schedules_detail/";
    private final String HOLIDAY_URL = "https://holidayapi.com/v1/holidays?pretty&key=97662a7f-e120-4e95-b3a8-7c18d1c40717&country=VN&year=2022";
    @Autowired
    private MailService mailService;

    List<Student> listStudent;
    List<StudentClass> listStudentClass;

    XSSFWorkbook workbook;
    XSSFSheet sheet;

    @GetMapping("/class-index")
    public String index(Model model, @CookieValue(name = "_token", defaultValue = "") String _token)
            throws JsonProcessingException {
        if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired"))
            return "redirect:/dashboard/login";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "get", HttpMethod.GET, request,
                String.class);
        List<Classses> classList = new ObjectMapper().readValue(response.getBody(),new TypeReference<>() {});
        model.addAttribute("classes", classList.stream().sorted(Comparator.comparingInt(Classses::getId).reversed()).toList());
        return "dashboard/class/class_index";
    }

    @GetMapping("/class-create")
    public String createClass(Model model, @CookieValue(name = "_token", defaultValue = "") String _token)
            throws JsonProcessingException {
        if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/dashboard/login";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> teacherResponse = restTemplate.exchange(TEACHER_URL + "list", HttpMethod.GET, request,
                String.class);
        List<Teacher> teacherList = new ObjectMapper().readValue(teacherResponse.getBody(), new TypeReference<>() {});

        ResponseEntity<ResponseModel> departmentResponse
                = restTemplate.exchange(DEPARTMENT_URL, HttpMethod.GET, request, ResponseModel.class);
        String jsonDepartments = new ObjectMapper().writeValueAsString(departmentResponse.getBody().getData());
        List<Department> departments = new ObjectMapper().readValue(jsonDepartments, new TypeReference<>() {
        });

        ResponseModel listMajor = restTemplate.getForObject(MAJOR_URL + "list", ResponseModel.class);
        List<Room> listRoom = restTemplate.getForObject(URL_ROOM, ArrayList.class);
        String json = new ObjectMapper().writeValueAsString(listRoom);
        List<Room> roomList = new ObjectMapper().readValue(json, new TypeReference<>() {
        });
        model.addAttribute("majors", listMajor.getData());
        model.addAttribute("departments", departments);
        model.addAttribute("teachers", teacherList);
        model.addAttribute("roomList", roomList);
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
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST,
                    request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            Classses classses = new ObjectMapper().readValue(json, Classses.class);
            HttpEntity<Object> request2 = new HttpEntity<>(headers);
            Room room = restTemplate.getForObject(URL_ROOM + classses.getRoomId(), Room.class);
            ResponseEntity<ResponseModel> response2 = restTemplate.exchange(
                    CLASS_URL + "findClassByMajorId/" + classses.getMajorId(), HttpMethod.GET, request2,
                    ResponseModel.class);
            String json2 = new ObjectMapper().writeValueAsString(response2.getBody().getData());
            List<Classses> listClass = new ObjectMapper().readValue(json2, new TypeReference<List<Classses>>() {
            });
            ResponseEntity<String> responseTeacher = restTemplate.exchange(TEACHER_URL + "list", HttpMethod.GET, request, String.class);
            List<Teacher> listTeacher = new ObjectMapper().readValue(responseTeacher.getBody(), new TypeReference<List<Teacher>>() {
            });
            model.addAttribute("class", classses);
            model.addAttribute("room", room);
            model.addAttribute("listTeacher", listTeacher);
            model.addAttribute("classList",
                    listClass.stream().filter(classses1 -> !classses1.getClassCode().equals(classses.getClassCode()))
                            .collect(Collectors.toList()));
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
        try {
            JWTUtils.checkExpired(_token);
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
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return e.getMessage();
            } else
                return e.getMessage();
        }

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
            ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "findClassCode", HttpMethod.POST,
                    request, String.class);

            return response.toString();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return e.getMessage();
            } else
                return e.getMessage();
        }
    }

    @GetMapping("/class-searchClasssesByClassCode")
    @ResponseBody
    public String searchClasssesByClassCode(Model model, @CookieValue(name = "_token", defaultValue = "") String _token,
                                            @RequestParam("classCode") String classCode) throws JsonProcessingException {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "searchClasssesByClassCode/" + classCode,
                    HttpMethod.GET, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return e.getMessage();
            } else
                return e.getMessage();
        }

    }

    @PostMapping("/create_schedule")
    @ResponseBody
    public String create_schedule(@CookieValue(name = "_token", defaultValue = "") String _token,
                                  @RequestParam("startDate") String startDate,
                                  @RequestParam("semester") Integer semester,
                                  @RequestParam("majorId") Integer majorId,
                                  @RequestParam("shift") String shift,
                                  @RequestParam("teacher_id") Integer teacherId,
                                  @RequestParam("classId") Integer classId) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.set("Authorization", "Bearer " + _token);
            Schedule schedule = new Schedule();
            MultiValueMap<String, Object> paramsCheck = new LinkedMultiValueMap<>();
            paramsCheck.add("classId", classId);
            paramsCheck.add("semester", semester);
            HttpEntity<MultiValueMap<String, Object>> requestCheck = new HttpEntity<>(paramsCheck, headers);
            ResponseEntity<ResponseModel> responseCheck = restTemplate.exchange(
                    SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestCheck, ResponseModel.class);
            if (responseCheck.getBody().getData() != null) {
                return "error";
            } else {
                if (semester.equals(1)) {
                    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                    Schedule scheduleParams = new Schedule();
                    scheduleParams.setEndDate("");
                    scheduleParams.setStartDate(startDate);
                    scheduleParams.setClassId(classId);
                    scheduleParams.setSemester(semester);
                    String json = objectMapper.writeValueAsString(scheduleParams);
                    params.add("schedule", json);
                    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
                    ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "post",
                            HttpMethod.POST, request, ResponseModel.class);
                    String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                    schedule = objectMapper.readValue(jsonSchedule, Schedule.class);

                    ResponseEntity<ResponseModel> responseMajor = restTemplate.getForEntity(
                            MAJOR_URL + "get/" + majorId,
                            ResponseModel.class);
                    String jsonMajor = objectMapper.writeValueAsString(responseMajor.getBody().getData());
                    Major major = objectMapper.readValue(jsonMajor, Major.class);

                    List<Subject> listSubject = major.getSubjectsById().stream()
                            .filter(subject -> subject.getSemesterId() == semester).toList();

                    LocalDate startDates = LocalDate.parse(startDate);
                    LocalDate endDateTemp = LocalDate.parse(startDate).plusYears(1);

                    ResponseEntity<HolidayModel> responseHoliday = restTemplate.getForEntity(HOLIDAY_URL,
                            HolidayModel.class);
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
                                    boolean isInHoliday = holidayList.stream()
                                            .noneMatch(holiday -> holiday.getDate().getMonthValue() == getMonth
                                                    && holiday.getDate().getDayOfMonth() == getDate);
                                    if (Arrays.stream(DayOfWeekSchedule.array0).anyMatch(dayOfWeek::equals)
                                            && isInHoliday) {
                                        ScheduleDetail scheduleDetail = new ScheduleDetail();
                                        if (i % 2 == 0) {
                                            scheduleDetail.setDayOfWeek(dayOfWeek);
                                            scheduleDetail.setDate(date.toString());
                                            scheduleDetail.setScheduleId(schedule.getId());
                                            scheduleDetail.setSubjectId(subject.getId());
                                            scheduleDetail.setTeacherId(teacherId);
                                            scheduleDetail.setShift(shift);
                                            scheduleDetail.setSlot(i);
                                            listScheduleDetails.add(scheduleDetail);
                                            slot++;
                                            break;
                                        } else {
                                            scheduleDetail.setDayOfWeek(dayOfWeek);
                                            scheduleDetail.setDate(date.toString());
                                            scheduleDetail.setScheduleId(schedule.getId());
                                            scheduleDetail.setSubjectId(subject.getId());
                                            scheduleDetail.setTeacherId(teacherId);
                                            scheduleDetail.setShift(shift);
                                            scheduleDetail.setSlot(i);
                                            listScheduleDetails.add(scheduleDetail);
                                            slot++;
                                            if (subject.getSlot() == slot) {
                                                startDates = LocalDate.parse(
                                                                listScheduleDetails.get(listScheduleDetails.size() - 1)
                                                                        .getDate())
                                                        .plusDays(1);
                                                break;
                                            }
                                        }
                                    } else {
                                        break;
                                    }
                                }
                                if (subject.getSlot() == slot) {
                                    startDates = LocalDate
                                            .parse(listScheduleDetails.get(listScheduleDetails.size() - 1).getDate())
                                            .plusDays(1);
                                    break;
                                }
                            }
                            if (listSubject.get(listSubject.size() - 1).equals(subject)
                                    && listSubject.get(listSubject.size() - 1).getSlot() == slot) {
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
                                    boolean isInHoliday = holidayList.stream()
                                            .noneMatch(holiday -> holiday.getDate().getMonthValue() == getMonth
                                                    && holiday.getDate().getDayOfMonth() == getDate);
                                    if (Arrays.stream(DayOfWeekSchedule.array1).anyMatch(dayOfWeek::equals)
                                            && isInHoliday) {
                                        ScheduleDetail scheduleDetail = new ScheduleDetail();
                                        if (i % 2 == 0) {
                                            scheduleDetail.setDayOfWeek(dayOfWeek);
                                            scheduleDetail.setDate(date.toString());
                                            scheduleDetail.setScheduleId(schedule.getId());
                                            scheduleDetail.setSubjectId(subject.getId());
                                            scheduleDetail.setTeacherId(teacherId);
                                            scheduleDetail.setShift(shift);
                                            scheduleDetail.setSlot(i);
                                            listScheduleDetails.add(scheduleDetail);
                                            slot++;
                                            break;
                                        } else {
                                            scheduleDetail.setDayOfWeek(dayOfWeek);
                                            scheduleDetail.setDate(date.toString());
                                            scheduleDetail.setScheduleId(schedule.getId());
                                            scheduleDetail.setSubjectId(subject.getId());
                                            scheduleDetail.setTeacherId(teacherId);
                                            scheduleDetail.setShift(shift);
                                            scheduleDetail.setSlot(i);
                                            listScheduleDetails.add(scheduleDetail);
                                            slot++;
                                            if (subject.getSlot() == slot) {
                                                startDates = LocalDate.parse(
                                                                listScheduleDetails.get(listScheduleDetails.size() - 1)
                                                                        .getDate())
                                                        .plusDays(1);
                                                break;
                                            }
                                        }
                                    } else {
                                        break;
                                    }
                                }
                                if (subject.getSlot() == slot) {
                                    startDates = LocalDate
                                            .parse(listScheduleDetails.get(listScheduleDetails.size() - 1).getDate())
                                            .plusDays(1);
                                    break;
                                }
                            }
                            if (listSubject.get(listSubject.size() - 1).equals(subject)
                                    && listSubject.get(listSubject.size() - 1).getSlot() == slot) {
                                break;
                            }
                        }
                    }
                    MultiValueMap<String, Object> paramsScheduleDetails = new LinkedMultiValueMap<>();
                    paramsScheduleDetails.add("listSchedule", objectMapper.writeValueAsBytes(listScheduleDetails));
                    HttpEntity<MultiValueMap<String, Object>> requestScheduleDetails = new HttpEntity<>(
                            paramsScheduleDetails, headers);
                    ResponseEntity<String> responseScheduleDetails = restTemplate.exchange(
                            SCHEDULE_DETAIL_URL + "saveAll",
                            HttpMethod.POST, requestScheduleDetails, String.class);
                    schedule.setEndDate(listScheduleDetails.get(listScheduleDetails.size() - 1).getDate().toString());
                    MultiValueMap<String, Object> paramsSchedule = new LinkedMultiValueMap<>();
                    paramsSchedule.add("schedule", objectMapper.writeValueAsBytes(schedule));
                    HttpEntity<MultiValueMap<String, Object>> requestSchedule = new HttpEntity<>(paramsSchedule,
                            headers);
                    ResponseEntity<ResponseModel> response = restTemplate.exchange(SCHEDULE_URL + "put", HttpMethod.PUT,
                            requestSchedule, ResponseModel.class);
                    return "success";
                } else if (semester > 1) {
                    // check start must be greater than last date of object list schedule details

                    MultiValueMap<String, Object> paramsScheduleSemesterBefore = new LinkedMultiValueMap<>();
                    paramsScheduleSemesterBefore.add("classId", classId);
                    paramsScheduleSemesterBefore.add("semester", semester - 1);
                    HttpEntity<MultiValueMap<String, Object>> requestScheduleSemesterBefore = new HttpEntity<>(
                            paramsScheduleSemesterBefore, headers);
                    ResponseEntity<ResponseModel> responseScheduleSemesterBefore = restTemplate.exchange(
                            SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestScheduleSemesterBefore,
                            ResponseModel.class);

                    if(responseScheduleSemesterBefore.getBody().getData()!=null){
                        String jsonConvert = objectMapper
                                .writeValueAsString(responseScheduleSemesterBefore.getBody().getData());
                        Schedule scheduleCheck = objectMapper.readValue(jsonConvert, Schedule.class);
                        LocalDate convertStartdate = LocalDate.parse(startDate);
                        ScheduleDetail lastDateOfList = scheduleCheck.getScheduleDetailsById().stream()
                                .sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate())))
                                .toList().get(scheduleCheck.getScheduleDetailsById().size() - 1);
                        if (convertStartdate.isBefore(LocalDate.parse(lastDateOfList.getDate())) || convertStartdate.equals(LocalDate.parse(lastDateOfList.getDate()))) {
                            return "error date";
                        } else {
                            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                            Schedule scheduleParams = new Schedule();
                            scheduleParams.setEndDate("");
                            scheduleParams.setStartDate(startDate);
                            scheduleParams.setClassId(classId);
                            scheduleParams.setSemester(semester);
                            String json = objectMapper.writeValueAsString(scheduleParams);
                            params.add("schedule", json);
                            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
                            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(SCHEDULE_URL + "post",
                                    HttpMethod.POST, request, ResponseModel.class);
                            String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                            schedule = objectMapper.readValue(jsonSchedule, Schedule.class);

                            ResponseEntity<ResponseModel> responseMajor = restTemplate.getForEntity(
                                    MAJOR_URL + "get/" + majorId,
                                    ResponseModel.class);
                            String jsonMajor = objectMapper.writeValueAsString(responseMajor.getBody().getData());
                            Major major = objectMapper.readValue(jsonMajor, Major.class);

                            List<Subject> listSubject = major.getSubjectsById().stream()
                                    .filter(subject -> subject.getSemesterId() == semester).toList();

                            LocalDate startDates = LocalDate.parse(startDate);
                            LocalDate endDateTemp = LocalDate.parse(startDate).plusYears(1);

                            ResponseEntity<HolidayModel> responseHoliday = restTemplate.getForEntity(HOLIDAY_URL,
                                    HolidayModel.class);
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
                                            boolean isInHoliday = holidayList.stream()
                                                    .noneMatch(holiday -> holiday.getDate().getMonthValue() == getMonth
                                                            && holiday.getDate().getDayOfMonth() == getDate);
                                            if (Arrays.stream(DayOfWeekSchedule.array0).anyMatch(dayOfWeek::equals)
                                                    && isInHoliday) {
                                                ScheduleDetail scheduleDetail = new ScheduleDetail();
                                                if (i % 2 == 0) {
                                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                                    scheduleDetail.setDate(date.toString());
                                                    scheduleDetail.setScheduleId(schedule.getId());
                                                    scheduleDetail.setSubjectId(subject.getId());
                                                    scheduleDetail.setTeacherId(teacherId);
                                                    scheduleDetail.setShift(shift);
                                                    scheduleDetail.setSlot(i);
                                                    listScheduleDetails.add(scheduleDetail);
                                                    slot++;
                                                    break;
                                                } else {
                                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                                    scheduleDetail.setDate(date.toString());
                                                    scheduleDetail.setScheduleId(schedule.getId());
                                                    scheduleDetail.setSubjectId(subject.getId());
                                                    scheduleDetail.setTeacherId(teacherId);
                                                    scheduleDetail.setShift(shift);
                                                    scheduleDetail.setSlot(i);
                                                    listScheduleDetails.add(scheduleDetail);
                                                    slot++;
                                                    if (subject.getSlot() == slot) {
                                                        startDates = LocalDate.parse(
                                                                        listScheduleDetails.get(listScheduleDetails.size() - 1)
                                                                                .getDate())
                                                                .plusDays(1);
                                                        break;
                                                    }
                                                }
                                            } else {
                                                break;
                                            }
                                        }
                                        if (subject.getSlot() == slot) {
                                            startDates = LocalDate
                                                    .parse(listScheduleDetails.get(listScheduleDetails.size() - 1)
                                                            .getDate())
                                                    .plusDays(1);
                                            break;
                                        }
                                    }
                                    if (listSubject.get(listSubject.size() - 1).equals(subject)
                                            && listSubject.get(listSubject.size() - 1).getSlot() == slot) {
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
                                            boolean isInHoliday = holidayList.stream()
                                                    .noneMatch(holiday -> holiday.getDate().getMonthValue() == getMonth
                                                            && holiday.getDate().getDayOfMonth() == getDate);
                                            if (Arrays.stream(DayOfWeekSchedule.array1).anyMatch(dayOfWeek::equals)
                                                    && isInHoliday) {
                                                ScheduleDetail scheduleDetail = new ScheduleDetail();
                                                if (i % 2 == 0) {
                                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                                    scheduleDetail.setDate(date.toString());
                                                    scheduleDetail.setScheduleId(schedule.getId());
                                                    scheduleDetail.setSubjectId(subject.getId());
                                                    scheduleDetail.setTeacherId(teacherId);
                                                    scheduleDetail.setShift(shift);
                                                    scheduleDetail.setSlot(i);
                                                    listScheduleDetails.add(scheduleDetail);
                                                    slot++;
                                                    break;
                                                } else {
                                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                                    scheduleDetail.setDate(date.toString());
                                                    scheduleDetail.setScheduleId(schedule.getId());
                                                    scheduleDetail.setSubjectId(subject.getId());
                                                    scheduleDetail.setTeacherId(teacherId);
                                                    scheduleDetail.setShift(shift);
                                                    scheduleDetail.setSlot(i);
                                                    scheduleDetail.setTeacherId(teacherId);
                                                    listScheduleDetails.add(scheduleDetail);
                                                    slot++;
                                                    if (subject.getSlot() == slot) {
                                                        startDates = LocalDate.parse(
                                                                        listScheduleDetails.get(listScheduleDetails.size() - 1)
                                                                                .getDate())
                                                                .plusDays(1);
                                                        break;
                                                    }
                                                }
                                            } else {
                                                break;
                                            }
                                        }
                                        if (subject.getSlot() == slot) {
                                            startDates = LocalDate
                                                    .parse(listScheduleDetails.get(listScheduleDetails.size() - 1)
                                                            .getDate())
                                                    .plusDays(1);
                                            break;
                                        }
                                    }
                                    if (listSubject.get(listSubject.size() - 1).equals(subject)
                                            && listSubject.get(listSubject.size() - 1).getSlot() == slot) {
                                        break;
                                    }
                                }
                            }
                            MultiValueMap<String, Object> paramsScheduleDetails = new LinkedMultiValueMap<>();
                            paramsScheduleDetails.add("listSchedule", objectMapper.writeValueAsBytes(listScheduleDetails));
                            HttpEntity<MultiValueMap<String, Object>> requestScheduleDetails = new HttpEntity<>(
                                    paramsScheduleDetails, headers);
                            ResponseEntity<String> responseScheduleDetails = restTemplate.exchange(
                                    SCHEDULE_DETAIL_URL + "saveAll",
                                    HttpMethod.POST, requestScheduleDetails, String.class);
                            schedule.setEndDate(
                                    listScheduleDetails.get(listScheduleDetails.size() - 1).getDate().toString());
                            MultiValueMap<String, Object> paramsSchedule = new LinkedMultiValueMap<>();
                            paramsSchedule.add("schedule", objectMapper.writeValueAsBytes(schedule));
                            HttpEntity<MultiValueMap<String, Object>> requestSchedule = new HttpEntity<>(paramsSchedule,
                                    headers);
                            ResponseEntity<ResponseModel> response = restTemplate.exchange(SCHEDULE_URL + "put",
                                    HttpMethod.PUT,
                                    requestSchedule, ResponseModel.class);
                            return "success";
                        }
                    }else{
                        return "error schedule";
                    }
                }
                return "";
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
            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(
                    SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestSchedule,
                    ResponseModel.class);
            if (responseSchedule.getBody().getData() != null) {
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
                        int year = LocalDate.parse(listSortDate.get(i).getDate()).getYear();
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
                                diw.setYear(year);
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
                            diw.setYear(year);
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
                            diw.setYear(year);
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
                            diw.setYear(year);
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
                        .sorted((a, b) -> a.getWeekOfYear().compareTo(b.getWeekOfYear())).sorted((a, b) -> a.getYear().compareTo(b.getYear())).toList();
                int year1 = listSort.get(0).getYear();
                int year2 = listSort.get(listSort.size() - 1).getYear();
                LocalDate lastDateOfYear1 = LocalDate.parse(year1 + "-12-31");
                LocalDate lastDateOfYear2 = LocalDate.parse(year1 + "-12-30");
                if (year2 > year1) {
                    listSort = listSort.stream().map(d -> {
                        if (d.getDate().equals(lastDateOfYear1) || d.getDate().equals(lastDateOfYear2)) {
                            d.setYear(year1);
                        }
                        return d;
                    }).collect(Collectors.toList());
                    List<DayInWeek> listYear_1 = listSort.stream().filter(d -> d.getYear().equals(year1))
                            .toList();
                    List<DayInWeek> listYear_2 = listSort.stream().filter(d -> d.getYear().equals(year2)).toList();

                    HashMap<Integer, List<DayInWeek>> hashMap1 = new HashMap<Integer, List<DayInWeek>>();
                    HashMap<Integer, List<DayInWeek>> hashMap2 = new HashMap<Integer, List<DayInWeek>>();

                    for (DayInWeek dayInWeek : listYear_1) {
                        Integer key = dayInWeek.getWeekOfYear();
                        if (hashMap1.containsKey(key)) {
                            List<DayInWeek> list = hashMap1.get(key);
                            list.add(dayInWeek);

                        } else {
                            List<DayInWeek> list = new ArrayList<DayInWeek>();
                            list.add(dayInWeek);
                            hashMap1.put(key, list);
                        }
                    }

                    for (DayInWeek dayInWeek : listYear_2) {
                        Integer key = dayInWeek.getWeekOfYear();
                        if (hashMap2.containsKey(key)) {
                            List<DayInWeek> list = hashMap2.get(key);
                            list.add(dayInWeek);

                        } else {
                            List<DayInWeek> list = new ArrayList<DayInWeek>();
                            list.add(dayInWeek);
                            hashMap2.put(key, list);
                        }
                    }

                    List<TimetableModel> list = new ArrayList<>();


                    HashMap<Integer, List<DayInWeek>> newMapSortedByKey1 = hashMap1.entrySet().stream()
                            .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                            .collect(Collectors.toMap(HashMap.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                    LinkedHashMap::new));

                    HashMap<Integer, List<DayInWeek>> newMapSortedByKey2 = hashMap2.entrySet().stream()
                            .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                            .collect(Collectors.toMap(HashMap.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                    LinkedHashMap::new));


                    for (Map.Entry<Integer, List<DayInWeek>> entry : newMapSortedByKey1.entrySet()) {
                        Integer key = entry.getKey();
                        List<DayInWeek> value = entry.getValue();
                        TimetableModel timetableModel = new TimetableModel();
                        timetableModel.setWeek(key);
                        timetableModel.setList(value);
                        list.add(timetableModel);
                    }

                    for (Map.Entry<Integer, List<DayInWeek>> entry : newMapSortedByKey2.entrySet()) {
                        Integer key = entry.getKey();
                        List<DayInWeek> value = entry.getValue();
                        TimetableModel timetableModel = new TimetableModel();
                        timetableModel.setWeek(key);
                        timetableModel.setList(value);
                        list.add(timetableModel);
                    }
                    return list;
                } else {
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
                    List<TimetableModel> list = new ArrayList<>();
                    HashMap<Integer, List<DayInWeek>> newMapSortedByKey = hashMap.entrySet().stream()
                            .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                            .collect(Collectors.toMap(HashMap.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                    LinkedHashMap::new));

                    for (Map.Entry<Integer, List<DayInWeek>> entry : newMapSortedByKey.entrySet()) {
                        Integer key = entry.getKey();
                        List<DayInWeek> value = entry.getValue();
                        TimetableModel timetableModel = new TimetableModel();
                        timetableModel.setWeek(key);
                        timetableModel.setList(value);
                        list.add(timetableModel);
                    }

                    return list;
                }
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
            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(
                    SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestSchedule,
                    ResponseModel.class);
            if (responseSchedule.getStatusCode().is2xxSuccessful()) {
                String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
                Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
                LocalDate date = LocalDate.parse(currenDate);
                int getDate = date.getDayOfMonth();
                int getMonth = date.getMonthValue();
                boolean isSameDate = schedule.getScheduleDetailsById().stream()
                        .anyMatch(dates -> LocalDate.parse(dates.getDate()).getDayOfMonth() == getDate
                                && LocalDate.parse(dates.getDate()).getMonthValue() == getMonth
                                && dates.getSlot().equals(slot));
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
//            String[] splitDate = newDate.split("/");
//            String formatDate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
            HttpEntity<Object> requestScheduleDetails = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(
                    SCHEDULE_DETAIL_URL + "get/" + Integer.parseInt(schedule_details_id), HttpMethod.GET,
                    requestScheduleDetails, ResponseModel.class);
            String jsonScheduleDetails = objectMapper.writeValueAsString(response.getBody().getData());
            ScheduleDetail scheduleDetail = objectMapper.readValue(jsonScheduleDetails, ScheduleDetail.class);
            String dayOfWeek = String.valueOf(LocalDate.parse(newDate).getDayOfWeek());
            scheduleDetail.setDate(newDate);
            scheduleDetail.setDayOfWeek(dayOfWeek);
            scheduleDetail.setSlot(slot);
            String convertToJson = objectMapper.writeValueAsString(scheduleDetail);
            MultiValueMap<String, Object> paramsDetails = new LinkedMultiValueMap<>();
            paramsDetails.add("schedule_details", convertToJson);
            HttpEntity<MultiValueMap<String, Object>> requestScheduleDetailsPUT = new HttpEntity<>(paramsDetails,
                    headers);
            ResponseEntity<ResponseModel> responseScheduleDetails = restTemplate.exchange(SCHEDULE_DETAIL_URL + "put",
                    HttpMethod.PUT, requestScheduleDetailsPUT, ResponseModel.class);
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
            @PathVariable("studentCard") String studentCard) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            JWTUtils.checkExpired(_token);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseEntity<String> response = restTemplate.exchange(
                    STUDENT_URL + "findStudentByStudentCard/" + studentCard, HttpMethod.GET, request, String.class);
            Student student = objectMapper.readValue(response.getBody(), new TypeReference<Student>() {
            });
            if (student != null) {
                String convertToJson = objectMapper.writeValueAsString(student);
                return convertToJson;
            } else {
                throw new ErrorHandler("Student: " + studentCard + " is not existed !");
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
            @RequestParam("classId") int classId) {
        try {
            JWTUtils.checkExpired(_token);

            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<String> requestGET;
            HttpEntity<MultiValueMap<String, String>> requestPOST;
            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            List<StudentSubject> studentSubjectList = new ArrayList<>();

            //Get Class by classId
            requestGET = new HttpEntity<>(headers);
            ResponseEntity<String> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + classId,
                    HttpMethod.GET, requestGET, String.class);
            ResponseModel responseModelClass = new ObjectMapper().readValue(responseClass.getBody(), new TypeReference<ResponseModel>() {
            });
            String jsonClass = new ObjectMapper().writeValueAsString(responseModelClass.getData());
            Classses classModel = new ObjectMapper().readValue(jsonClass, Classses.class);

            //Add value to studentSubjectList
            for (Subject subject : classModel.getMajor().getSubjectsById()) {
                StudentSubject studentSubject = new StudentSubject();
                studentSubject.setStudentId(studentId);
                studentSubject.setSubjectId(subject.getId());
                studentSubject.setStatus("0");

                content.add("subjectId", String.valueOf(subject.getId()));
                content.add("studentId", String.valueOf(studentId));
                requestPOST = new HttpEntity<>(content, headers);
                ResponseEntity<String> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "findStudentSubjectBySubjectIdAndStudentId",
                        HttpMethod.POST, requestPOST, String.class);
                ResponseModel responseModelStudentSubject = objectMapper.readValue(responseStudentSubject.getBody(), new TypeReference<>() {
                });
                if (responseModelStudentSubject.getData() != null) {
                    String jsonStudentSubject = objectMapper.writeValueAsString(responseModelStudentSubject.getData());
                    StudentSubject studentSubjectModel = objectMapper.readValue(jsonStudentSubject, StudentSubject.class);
                    studentSubject.setId(studentSubjectModel.getId());
                }
                content.remove("subjectId");
                content.remove("studentId");

                studentSubjectList.add(studentSubject);
            }

            content.add("student_subject", objectMapper.writeValueAsString(studentSubjectList));
            requestPOST = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> responseStudentSubject = restTemplate
                    .exchange(STUDENT_SUBJECT_URL + "updateAll", HttpMethod.POST, requestPOST, ResponseModel.class);
            content.remove("student_subject");

            if (!responseStudentSubject.getStatusCode().is2xxSuccessful()) {
                throw new ErrorHandler("Add students failed !");
            }

            StudentClass studentClass = new StudentClass();
            studentClass.setStudentId(studentId);
            studentClass.setClassId(classId);

            requestPOST = new HttpEntity<>(content, headers);
            content.add("newStudentClass", objectMapper.writeValueAsString(studentClass));
            requestPOST = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "save",
                    HttpMethod.POST, requestPOST, ResponseModel.class);

            if (responseStudentClass.getStatusCode().is2xxSuccessful()) {
                String convertToJson = new ObjectMapper().writeValueAsString(responseStudentClass.getBody().getData());
                return convertToJson;
            } else {
                throw new ErrorHandler("Add students failed !");
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
            @RequestParam(name = "studentList", required = false) MultipartFile file) {
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
                    throw new ErrorHandler(e.getMessage());
                }
            } else {
                throw new ErrorHandler("Can not import empty list !");
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
                              @RequestParam(name = "file", required = false) MultipartFile file) throws JsonProcessingException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);

//            boolean flag = false;
//            HttpEntity<Object> requestClass = new HttpEntity<>(headers);
//            Classses classses = new ObjectMapper().readValue(newClass, Classses.class);

            MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            content.add("newClass", newClass);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "save", HttpMethod.POST, request,
                    ResponseModel.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Classses classModel = objectMapper.readValue(newClass, Classses.class);
                Object data = response.getBody().getData();
                if (file != null) {
                    try {
                        ClassResponse result = importStudentClass(_token, file, classModel.getClassCode(),
                                classModel.getLimitStudent());
                        if (result.getStatus() == "success") {
                            return result.toString();
                        } else {
                            return new ClassResponse("success",
                                    "Add student fail. " + result.getMessage()).toString();
                        }
                    } catch (Exception e) {
                        String message = StringUtils.substringBetween(e.getMessage(), "\"", "\"");
                        return new ClassResponse("success", "Add student fail. " + message).toString();
                    }
                } else {
                    return new ClassResponse("success", "").toString();
                }
            } else {
                return new ClassResponse("fail", "").toString();
            }

//            Room room = restTemplate.getForObject(URL_ROOM + classses.getRoomId(), Room.class);
//            ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "findClassByRoom/" + room.getId(), HttpMethod.GET, requestClass, ResponseModel.class);
//            String jsonClassList = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
//            List<Classses> list = new ObjectMapper().readValue(jsonClassList, new TypeReference<List<Classses>>() {
//            });
//            if (list != null) {
//                boolean isCheck = list.stream().anyMatch(classses1 -> classses1.getShift().equals(classses.getShift()));
//                if (isCheck) {
//                    flag = true;
//                }
//            } else {
//                flag = false;
//            }
//            if (!flag) {
//                MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
//                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//                content.add("newClass", newClass);
//
//                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);
//                ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "save", HttpMethod.POST, request,
//                        ResponseModel.class);
//                if (response.getStatusCode().is2xxSuccessful()) {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    Classses classModel = objectMapper.readValue(newClass, Classses.class);
//                    Object data = response.getBody().getData();
//                    if (file != null) {
//                        try {
//                            ClassResponse result = importStudentClass(_token, file, classModel.getClassCode(),
//                                    classModel.getLimitStudent());
//                            if (result.getStatus() == "success") {
//                                return result.toString();
//                            } else {
//                                return new ClassResponse("success",
//                                        "Add student fail. " + result.getMessage()).toString();
//                            }
//                        } catch (Exception e) {
//                            String message = StringUtils.substringBetween(e.getMessage(), "\"", "\"");
//                            return new ClassResponse("success", "Add student fail. " + message).toString();
//                        }
//                    } else {
//                        return new ClassResponse("success", "").toString();
//                    }
//                } else {
//                    return new ClassResponse("fail", "").toString();
//                }
//            } else {
//                return new ClassResponse("fail", "").toString();
//            }
        } catch (HttpClientErrorException ex) {
            log.error(ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ex.getMessage();
            } else {
                return ex.getMessage();
            }
        }

    }

    private ClassResponse importStudentClass(String _token, MultipartFile file, String classCode, int availablePlace)
            throws JsonProcessingException {
        if (!file.isEmpty()) {
            try {
                // get list studentId form Excel file
                List<String> listStudentCard = new ArrayList<>();
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet sheet = workbook.getSheetAt(0);
                for (int rowIndex = 0; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 1); rowIndex++) {
                    XSSFRow row = sheet.getRow(rowIndex);
                    if (rowIndex < 1) {
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

                    //List
                    List<StudentClass> studentClassList = new ArrayList<>();
                    List<StudentSubject> studentSubjectList = new ArrayList<>();

                    MultiValueMap<String, String> content = new LinkedMultiValueMap<>();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(content, headers);

                    ResponseEntity<String> responseStudent = restTemplate.exchange(
                            STUDENT_URL + "findStudentIdByRangeStudentCard/"
                                    + new ObjectMapper().writeValueAsString(listStudentCard),
                            HttpMethod.GET, request, String.class);
                    List<Student> listStudent = objectMapper.readValue(responseStudent.getBody(), new TypeReference<>() {
                    });
                    if (listStudent.size() > availablePlace) {
                        return new ClassResponse("error", "Chỉ được thêm " + availablePlace + " vào lớp");
                    }

                    content.add("classCode", classCode);
                    request = new HttpEntity<>(content, headers);
                    ResponseEntity<String> responseClass = restTemplate.exchange(CLASS_URL + "findClassCode",
                            HttpMethod.POST, request, String.class);
                    content.remove("classCode");
                    ResponseModel responseModel = objectMapper.readValue(responseClass.getBody(),
                            new TypeReference<ResponseModel>() {
                            });
                    String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
                    Classses classModel = objectMapper.readValue(convertToJson, Classses.class);

                    for (Student student : listStudent) {
                        for (StudentClass studentClass : classModel.getStudentClassById()) {
                            var card1 = studentClass.getClassStudentByStudent().getStudentCard();
                            var card2 = student.getStudentCard();
                            if (studentClass.getClassStudentByStudent().getStudentCard().equals(student.getStudentCard())) {
                                throw new ErrorHandler("Student: " + student.getStudentByProfile().getFullName() + " already in thís class !");
                            }
                        }
                        StudentClass studentClass = new StudentClass();
                        studentClass.setStudentId(student.getId());
                        studentClass.setClassId(classModel.getId());
                        studentClassList.add(studentClass);

                        //Add value to studentSubjectList
                        for (Subject subject : classModel.getMajor().getSubjectsById()) {
                            StudentSubject studentSubject = new StudentSubject();
                            studentSubject.setStudentId(student.getId());
                            studentSubject.setSubjectId(subject.getId());
                            studentSubject.setStatus("0");

                            content.add("subjectId", String.valueOf(subject.getId()));
                            content.add("studentId", String.valueOf(student.getId()));
                            request = new HttpEntity<>(content, headers);

                            ResponseEntity<String> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "findStudentSubjectBySubjectIdAndStudentId",
                                    HttpMethod.POST, request, String.class);
                            ResponseModel responseModelStudentSubject = objectMapper.readValue(responseStudentSubject.getBody(), new TypeReference<>() {
                            });
                            if (responseModelStudentSubject.getData() != null) {
                                String jsonStudentSubject = objectMapper.writeValueAsString(responseModelStudentSubject.getData());
                                StudentSubject studentSubjectModel = objectMapper.readValue(jsonStudentSubject, StudentSubject.class);
                                studentSubject.setId(studentSubjectModel.getId());
                            }
                            content.remove("subjectId");
                            content.remove("studentId");

                            studentSubjectList.add(studentSubject);
                        }
                    }

                    //POST ALL STUDENT SUBJECT
                    content.add("student_subject", objectMapper.writeValueAsString(studentSubjectList));
                    request = new HttpEntity<>(content, headers);
                    ResponseEntity<ResponseModel> responseStudentSubject = restTemplate
                            .exchange(STUDENT_SUBJECT_URL + "updateAll", HttpMethod.POST, request, ResponseModel.class);
                    content.remove("student_subject");

                    if (!responseStudentSubject.getStatusCode().is2xxSuccessful()) {
                        return new ClassResponse("error", "Import student list failed");
                    }

                    //Post All StudentClass to DB
                    content.add("listStudentClass", objectMapper.writeValueAsString(studentClassList));
                    request = new HttpEntity<>(content, headers);
                    ResponseEntity<ResponseModel> responseStudentClass = restTemplate
                            .exchange(STUDENT_CLASS_URL + "saveAll", HttpMethod.POST, request, ResponseModel.class);
                    content.remove("listStudentClass");

                    if (responseStudentClass.getStatusCode().is2xxSuccessful()) {
                        return new ClassResponse("success",
                                "Add " + studentClassList.size() + " students success");
                    } else {
                        return new ClassResponse("error", "Import student list failed");
                    }
                } else {
                    return new ClassResponse("error", "Can not import empty list");
                }
            } catch (Exception e) {
                var s = e.getMessage();
                throw new RuntimeException(e.getMessage());
            }
        }
        return new ClassResponse("error", "Can not import empty list");
    }

    @GetMapping("/export-student-excel/{ClassId}")
    public void exportStudentList(HttpServletResponse response,
                                  @CookieValue(name = "_token", defaultValue = "") String _token,
                                  @PathVariable("ClassId") int classId) {
        try {
            JWTUtils.checkExpired(_token);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ObjectMapper objectMapper = new ObjectMapper();

            ResponseEntity<String> responseClass = restTemplate.exchange(CLASS_URL + "findOne/" + classId,
                    HttpMethod.GET, request, String.class);
            ResponseModel responseModel = objectMapper.readValue(responseClass.getBody(), new TypeReference<>() {
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

    @PostMapping("/checkTeacherChange")
    @ResponseBody
    public Object checkTeacherChange(@CookieValue(name = "_token", defaultValue = "") String _token,
                                     @RequestParam("card") String card, @RequestParam("shift") String shift) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.add("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<Teacher> response = restTemplate.exchange(TEACHER_URL + "getByCard/" + card, HttpMethod.GET,
                    request, Teacher.class);
            ResponseEntity<ResponseModel> responseListClass = restTemplate.exchange(CLASS_URL + "findClassByTeacher/" + response.getBody().getId(), HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(responseListClass.getBody().getData());
            List<Classses> classsesList = objectMapper.readValue(json, new TypeReference<List<Classses>>() {
            });
            boolean isCheck = classsesList.stream().anyMatch(classses -> classses.getShift().equals(shift));
            if (isCheck) {
                return "error";
            } else {
                return "success";
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
                                 @RequestParam("classId") Integer classId, @RequestParam("teacherCard") String teacherCard)
            throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + _token);
            ObjectMapper objectMapper = new ObjectMapper();
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "getClass/" + classId,
                    HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            Classses classses = objectMapper.readValue(json, Classses.class);
            HttpEntity<Object> requestTeacher = new HttpEntity<>(headers);
            ResponseEntity<String> responseTeacher = restTemplate.exchange(TEACHER_URL + "getByCard/" + teacherCard,
                    HttpMethod.GET, request, String.class);
            Teacher teacher = objectMapper.readValue(responseTeacher.getBody(), Teacher.class);
            classses.setTeacherId(teacher.getId());

            String jsonClass = objectMapper.writeValueAsString(classses);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("class", jsonClass);
            HttpEntity<MultiValueMap<String, Object>> requestUpdateClass = new HttpEntity<>(params, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(CLASS_URL + "updateClass", HttpMethod.PUT,
                    requestUpdateClass, String.class);
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
            ResponseEntity<ResponseModel> responseSchedule = restTemplate.exchange(
                    SCHEDULE_URL + "getScheduleByClassAndSemester", HttpMethod.POST, requestSchedule,
                    ResponseModel.class);
            String jsonSchedule = objectMapper.writeValueAsString(responseSchedule.getBody().getData());
            Schedule schedule = objectMapper.readValue(jsonSchedule, Schedule.class);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "getClass/" + classId,
                    HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            Classses classses = objectMapper.readValue(json, Classses.class);

            ScheduleModel scheduleModel = new ScheduleModel();
            List<DayInWeek> dayInWeekList = new ArrayList<>();
            boolean flag = false;
            LocalDate minDate = schedule.getScheduleDetailsById().stream()
                    .map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).min(LocalDate::compareTo).get();
            LocalDate maxDate = schedule.getScheduleDetailsById().stream()
                    .map(scheduleDetail -> LocalDate.parse(scheduleDetail.getDate())).max(LocalDate::compareTo).get();
            List<ScheduleDetail> listSortDate = schedule.getScheduleDetailsById().stream()
                    .sorted((a, b) -> LocalDate.parse(a.getDate()).compareTo(LocalDate.parse(b.getDate()))).toList();
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
                        if (listSortDate.get(listSortDate.size() - 1).equals(listSortDate.get(i))
                                && date.equals(LocalDate.parse(listSortDate.get(listSortDate.size() - 1).getDate()))) {
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
                        if (LocalDate.parse(listSortDate.get(listSortDate.indexOf(listSortDate.get(i)) + 1).getDate())
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
            ResponseEntity<ResponseModel> response = restTemplate.exchange(CLASS_URL + "getClass/" + classId,
                    HttpMethod.GET, request, ResponseModel.class);
            String json = new ObjectMapper().writeValueAsString(response.getBody().getData());
            Classses classses = new ObjectMapper().readValue(json, Classses.class);
            File attachment = FileUtils.convertMultiPartToFile(file);
            for (StudentClass student : classses.getStudentClassById()) {
                // Send mail
                Mail mail = new Mail();
                mail.setToMail(student.getClassStudentByStudent().getStudentByProfile().getEmail());
                mail.setSubject("Account student HKT SYSTEM");
                String name = student.getClassStudentByStudent().getStudentByProfile().getFirstName() + " "
                        + student.getClassStudentByStudent().getStudentByProfile().getLastName();
                Map<String, Object> props = new HashMap<>();
                props.put("fullname", name);
                mail.setProps(props);
                mailService.sendHtmlMessageSendSchedule(mail, attachment);
                // -----------------------------
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
            ResponseEntity<ResponseModel> response = restTemplate.exchange(
                    STUDENT_CLASS_URL + "getStudent/" + studentId, HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            List<StudentClass> studentClassList = objectMapper.readValue(json, new TypeReference<List<StudentClass>>() {
            });
            boolean isExistsInClass = studentClassList.stream()
                    .anyMatch(studentClass -> studentClass.getClassId().equals(classId));
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
            ResponseEntity<ResponseModel> response = restTemplate.exchange(
                    STUDENT_CLASS_URL + "getStudent/" + studentId, HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            List<StudentClass> studentClassList = objectMapper.readValue(json, new TypeReference<List<StudentClass>>() {
            });
            StudentClass studentClass = studentClassList.stream()
                    .filter(studentClass1 -> studentClass1.getClassId().equals(classIdCurrent)
                            && studentClass1.getStudentId().equals(studentId))
                    .findFirst().get();
            studentClass.setClassId(classIdChange);
            String jsonRequest = objectMapper.writeValueAsString(studentClass);
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("studentClass", jsonRequest);
            HttpEntity<MultiValueMap<String, Object>> request2 = new HttpEntity<>(params, headers);
            ResponseEntity<ResponseModel> response2 = restTemplate.exchange(STUDENT_CLASS_URL + "put", HttpMethod.PUT,
                    request2, ResponseModel.class);
            if (response2.getStatusCode().is2xxSuccessful()) {
                return "success";
            } else {
                return "error";
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ex.getMessage();
        }
    }

    @PostMapping("checkTeacherScheduleDetailsChange")
    @ResponseBody
    public Object checkTeacherScheduleDetailsChange(@CookieValue(name = "_token", defaultValue = "") String _token,
                                                    @RequestParam("date_change") String date_change,
                                                    @RequestParam("slot") Integer slot,
                                                    @RequestParam("shift") String shift,
                                                    @RequestParam("teacherCode") String teacherCode) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + _token);
            ObjectMapper objectMapper = new ObjectMapper();
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<String> responseTeacher = restTemplate.exchange(TEACHER_URL + "getByCard/" + teacherCode,
                    HttpMethod.GET, request, String.class);
            Teacher teacher = objectMapper.readValue(responseTeacher.getBody(), Teacher.class);

            ResponseEntity<ResponseModel> response = restTemplate.exchange(SCHEDULE_DETAIL_URL + "findScheduleByTeacher/" + teacher.getId(), HttpMethod.GET, request, ResponseModel.class);
            String json = objectMapper.writeValueAsString(response.getBody().getData());
            List<ScheduleDetail> scheduleDetails = objectMapper.readValue(json, new TypeReference<List<ScheduleDetail>>() {
            });
            scheduleDetails = scheduleDetails.stream().filter(scheduleDetail -> LocalDate.parse(date_change).equals(LocalDate.parse(scheduleDetail.getDate()))
                    && scheduleDetail.getShift().equals(shift) && scheduleDetail.getSlot().equals(slot)).toList();
            if (scheduleDetails.isEmpty()) {
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

    @PostMapping("changeTeacherInScheduleDetail")
    @ResponseBody
    public Object changeTeacherInScheduleDetail(@CookieValue(name = "_token", defaultValue = "") String _token,
                                                @RequestParam("schedule_detail_id") Integer schedule_detail_id,
                                                @RequestParam("teacher_card") String teacher_card) throws JsonProcessingException {
        try {
            JWTUtils.checkExpired(_token);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + _token);
            ObjectMapper objectMapper = new ObjectMapper();
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ResponseEntity<ResponseModel> response = restTemplate.exchange(
                    SCHEDULE_DETAIL_URL + "get/" + schedule_detail_id, HttpMethod.GET,
                    request, ResponseModel.class);
            String jsonScheduleDetails = objectMapper.writeValueAsString(response.getBody().getData());
            ResponseEntity<String> responseTeacher = restTemplate.exchange(TEACHER_URL + "getByCard/" + teacher_card,
                    HttpMethod.GET, request, String.class);
            Teacher teacher = objectMapper.readValue(responseTeacher.getBody(), Teacher.class);
            ScheduleDetail scheduleDetail = objectMapper.readValue(jsonScheduleDetails, ScheduleDetail.class);
            scheduleDetail.setTeacherId(teacher.getId());
            String convertToJson = objectMapper.writeValueAsString(scheduleDetail);
            MultiValueMap<String, Object> paramsDetails = new LinkedMultiValueMap<>();
            paramsDetails.add("schedule_details", convertToJson);
            HttpEntity<MultiValueMap<String, Object>> requestScheduleDetailsPUT = new HttpEntity<>(paramsDetails,
                    headers);
            ResponseEntity<ResponseModel> responseScheduleDetails = restTemplate.exchange(SCHEDULE_DETAIL_URL + "put",
                    HttpMethod.PUT, requestScheduleDetailsPUT, ResponseModel.class);
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

    @PostMapping("getAvailableRoom")
    @ResponseBody
    public String getAvailableRoom(@CookieValue(name = "_token") String _token,
                                   @RequestParam(value = "date", required = false) String inputDate,
                                   @RequestParam("departmentId") String departmentId,
                                   @RequestParam("shift") String shift) {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/dashboad/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + _token);

            ObjectMapper objectMapper = new ObjectMapper();
            HttpEntity<Object> requestGET = new HttpEntity<>(headers);

            HttpEntity<MultiValueMap<String, Object>> requestPOST;
            MultiValueMap<String, Object> content;

            //@RequestParam processing
            LocalDate date;
            if (inputDate == null || inputDate == ""){
                date = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }else {
                date = LocalDate.parse(inputDate);
            }

            //Get All Room By departmentId
            HttpEntity<String> responseRoom = restTemplate.exchange(
                    URL_ROOM + "findRoomsByDepartmentId/" + departmentId,
                    HttpMethod.GET, requestGET, String.class);
            String jsonRooms = objectMapper.writeValueAsString(responseRoom.getBody());
            List<Room> roomList = objectMapper.readValue(responseRoom.getBody(), new TypeReference<>() {
            });

            //Get All class by departmentId
            HttpEntity<ResponseModel> responseClass = restTemplate.exchange(
                    CLASS_URL + "findClassesDepartmentId/" + departmentId,
                    HttpMethod.GET, requestGET, ResponseModel.class);

            String jsonResponseModelClasses = objectMapper.writeValueAsString(responseClass.getBody().getData());
            List<Classses> classsesList = objectMapper.readValue(jsonResponseModelClasses, new TypeReference<>() {
            });
            if (classsesList.size() != 0) {
                //Set endate for class
                for (Classses clazz : classsesList) {
                    if (clazz.getSchedulesById().size() > 0) {
                        clazz.setEndDate(clazz.getSchedulesById().stream()
                                .sorted(Comparator.comparing(Schedule::getEndDate).reversed())
                                .toList().get(0).getEndDate());
                    }
                }

                classsesList = classsesList.stream()
                        .filter(clazz -> clazz.getEndDate() != null)
                        .filter(clazz -> clazz.getShift().equals(shift) && LocalDate.parse(clazz.getEndDate()).isAfter(date))
                        .filter(StreamHelper.distinctByKey(Classses::getRoomId))
                        .collect(Collectors.toList());
                if (classsesList.size() != 0) {
                    List<Room> availableRooms = new ArrayList<>();
                    for (Room room : roomList) {
                        if (classsesList.stream().anyMatch(clazz -> clazz.getRoomId().equals(room.getId()))){
                            continue;
                        }else {
                            availableRooms.add(room);
                        }
                    }
                    return objectMapper.writeValueAsString(availableRooms);
                }
                return objectMapper.writeValueAsString(roomList);
            }
            return objectMapper.writeValueAsString(roomList);

        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @PostMapping("/class-update-room")
    @ResponseBody
    public Object class_update_room(@CookieValue(name = "_token", defaultValue = "") String _token,
                                    @RequestParam("classId") Integer classId,
                                    @RequestParam("roomId") int roomId) throws JsonProcessingException {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/dashboard/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<Object> request = new HttpEntity<>(headers);
            ObjectMapper objectMapper = new ObjectMapper();

            ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "findOne/" + classId,
                    HttpMethod.GET, request, String.class);
            ResponseModel responseModel = objectMapper.readValue(response.getBody(), new TypeReference<ResponseModel>() {
            });
            String convertToJson = objectMapper.writeValueAsString(responseModel.getData());
            Classses classModel = objectMapper.readValue(convertToJson, Classses.class);

            classModel.setRoomId(roomId);

            MultiValueMap<String, Object> content = new LinkedMultiValueMap<>();
            content.add("newClass", objectMapper.writeValueAsString(classModel));
            HttpEntity<MultiValueMap<String, Object>> requestPOST = new HttpEntity<>(content, headers);

            ResponseEntity<ResponseModel> responsePostClass = restTemplate.exchange(CLASS_URL + "save",
                    HttpMethod.POST, requestPOST, ResponseModel.class);
            if (responsePostClass.getStatusCode().is2xxSuccessful()) {
                return "success";
            } else {
                throw new ErrorHandler("Change class failed");
            }
        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @PostMapping("getAvailableTeacher")
    @ResponseBody
    public String getAvailableTeacher(@CookieValue(name = "_token") String _token,
                                      @RequestParam("shift") String shift,
                                      @RequestParam("date") String inputDate) {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/dashboad/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + _token);

            ObjectMapper objectMapper = new ObjectMapper();
            HttpEntity<Object> requestGET = new HttpEntity<>(headers);

            HttpEntity<MultiValueMap<String, Object>> requestPOST;
            MultiValueMap<String, Object> content;

            //@RequestParam processing
            LocalDate date = LocalDate.parse(inputDate);

            //Get All Teacher
            HttpEntity<String> responseTeachers = restTemplate.exchange(
                    TEACHER_URL + "list",
                    HttpMethod.GET, requestGET, String.class);
            List<Teacher> allTeachers = objectMapper.readValue(responseTeachers.getBody(), new TypeReference<>() {
            });

            HttpEntity<ResponseModel> responseClass = restTemplate.exchange(
                    CLASS_URL + "findClassesByShift/" + shift,
                    HttpMethod.GET, requestGET, ResponseModel.class);

            String jsonResponseModelClasses = objectMapper.writeValueAsString(responseClass.getBody().getData());
            List<Classses> classesByShift = objectMapper.readValue(jsonResponseModelClasses, new TypeReference<>() {
            });
            if (classesByShift.size() > 0) {

                //Set endate for class
                for (Classses clazz : classesByShift) {
                    if (clazz.getSchedulesById().size() > 0) {
                        clazz.setEndDate(clazz.getSchedulesById().stream()
                                .sorted(Comparator.comparing(Schedule::getEndDate).reversed())
                                .toList().get(0).getEndDate());
                    }
                }

                //filter classesByShift
                classesByShift = classesByShift.stream()
                        .filter(clazz -> clazz.getEndDate() != null)
                        .filter(clazz -> LocalDate.parse(clazz.getEndDate()).isAfter(date))
                        .filter(StreamHelper.distinctByKey(Classses::getTeacherId))
                        .toList();
                if (classesByShift.size() != 0) {
                    List<Teacher> availableTeachers = new ArrayList<>();
                    for (Teacher teacher : allTeachers) {
                        if (!classesByShift.stream().anyMatch(s1 -> s1.getTeacherId().equals(teacher.getId()))){
                            availableTeachers.add(teacher);
                        }

                    }
                    return objectMapper.writeValueAsString(availableTeachers);
                }
                return objectMapper.writeValueAsString(allTeachers);
            }

            return objectMapper.writeValueAsString(allTeachers);

        } catch (Exception e) {
            throw new ErrorHandler(e.getMessage());
        }
    }

    @GetMapping("get-mark/{subjectId}/{studentId}")
    @ResponseBody
    public Object getMarkBySubjectIdAndStudentId(@CookieValue(name = "_token", defaultValue = "") String _token,
                                                 @PathVariable("subjectId") int subjectId,
                                                 @PathVariable("studentId") int studentId      ) {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<String> requestGET = new HttpEntity<>(headers);
            HttpEntity<MultiValueMap<String, Object>> resquestPOST;
            MultiValueMap<String, Object> content;
            ObjectMapper objectMapper = new ObjectMapper();

            content = new LinkedMultiValueMap<>();
            content.add("subjectId", subjectId);
            content.add("studentId", studentId);
            resquestPOST = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(
                    STUDENT_SUBJECT_URL +"findStudentSubjectBySubjectIdAndStudentId",
                    HttpMethod.POST,resquestPOST, ResponseModel.class);
            String jsonStudentSubject = objectMapper.writeValueAsString(responseStudentSubject.getBody().getData());

            StudentSubject studentSubject = objectMapper.readValue(jsonStudentSubject, new TypeReference<>(){});
            if (studentSubject == null){
                throw new ErrorHandler("Dont have any record for this student");
            }
            ResponseEntity<Mark> responseMark = restTemplate.exchange(
                    MARK_URL + "findMarkByStudentSubjectId/" + studentSubject.getId(),
                    HttpMethod.GET, requestGET, Mark.class);
            Mark mark = responseMark.getBody();

            if (mark == null){
                throw new ErrorHandler("This student have no mark in this subject to update !");
            }
            return objectMapper.writeValueAsString(mark);

        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @PostMapping("/update-mark")
    @ResponseBody
    public Object updateMark(@CookieValue(name = "_token", defaultValue = "") String _token,
                             @RequestParam("newMark") String jsonMark) {
        try {
            if (JWTUtils.isExpired(_token).equalsIgnoreCase("token expired")) return "redirect:/login";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + _token);
            HttpEntity<String> requestGET = new HttpEntity<>(headers);
            HttpEntity<MultiValueMap<String, Object>> resquestPOST;
            MultiValueMap<String, Object> content;
            ObjectMapper objectMapper = new ObjectMapper();

            Mark newMark = objectMapper.readValue(jsonMark, new TypeReference<>(){});

            ResponseEntity<ResponseModel> responseMark = restTemplate.exchange(
                    MARK_URL + "get/" + newMark.getId(),
                    HttpMethod.GET, requestGET, ResponseModel.class);
            String jsonResponseMark = objectMapper.writeValueAsString(responseMark.getBody().getData());
            Mark mark = objectMapper.readValue(jsonResponseMark, new TypeReference<>(){});

            if (mark == null){
                throw new ErrorHandler("Dont find any Mark record fo this student");
            }
            mark.setAsm(newMark.getAsm());
            mark.setObj(newMark.getObj());
            mark.setUpdateTimes(mark.getUpdateTimes()+1);


            //Save markList
            content = new LinkedMultiValueMap<>();
            content.add("mark", objectMapper.writeValueAsString(mark));
            resquestPOST = new HttpEntity<>(content, headers);
            ResponseEntity<ResponseModel> responseSaveMark = restTemplate.exchange(MARK_URL + "save",
                    HttpMethod.POST, resquestPOST, ResponseModel.class);

            return "success";

        } catch (Exception ex) {
            throw new ErrorHandler(ex.getMessage());
        }
    }

    @PostMapping("/import-excel-student")
    @ResponseBody
    public Object studentImport(@CookieValue(name = "_token") String _token,
                                @RequestParam("file") MultipartFile file,
                                @RequestParam("availablePlace") Integer availablePlace,
                                @RequestParam("classId") Integer classId) {
        try {
            String isExpired = JWTUtils.isExpired(_token);
            if (!isExpired.toLowerCase().equals("token expired")) {
                if (!file.isEmpty()) {
                    if (FileUtils.getExtension(file.getOriginalFilename()).equals("xlsx")) {
                        listStudent = new ArrayList<>();
                        listStudentClass = new ArrayList<>();
                        boolean flag = false;
                        try {
                            workbook = new XSSFWorkbook(file.getInputStream());
                            sheet = workbook.getSheetAt(0);
                            RestTemplate restTemplate = new RestTemplate();
                            HttpHeaders headers = new HttpHeaders();
                            headers.set("Authorization", "Bearer " + _token);
                            HttpEntity<String> request = new HttpEntity<>(headers);

                            String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                            String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
                            String numbers = "1234567890";
                            String combinedChars = capitalCaseLetters + lowerCaseLetters + numbers;

                            //Select role
                            HttpHeaders headersRole = new HttpHeaders();
                            headersRole.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                            MultiValueMap<String, String> paramRole = new LinkedMultiValueMap<>();
                            String roleName = "STUDENT";
                            paramRole.add("role", roleName);
                            HttpEntity<MultiValueMap<String, String>> requestRole = new HttpEntity<>(paramRole, headersRole);
                            ResponseEntity<String> responseRole = restTemplate.exchange(ROLE_URL + "get", HttpMethod.POST, requestRole, String.class);
                            Role role = new ObjectMapper().readValue(responseRole.getBody(), Role.class);

                            int excelLength = ExcelHelper.getNumberOfNonEmptyCells(sheet, 2);
                            if (excelLength <= availablePlace + 1) {
                                for (int rowIndex = 2; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
                                    XSSFRow row = sheet.getRow(rowIndex);
                                    Cell date = row.getCell(4);

                                    String first_name = ExcelHelper.getValue(row.getCell(1)).toString();
                                    String last_name = ExcelHelper.getValue(row.getCell(2)).toString();
                                    String email = ExcelHelper.getValue(row.getCell(3)).toString();
                                    String dob = "";
                                    if (date != null) {
                                        dob = date.getLocalDateTimeCellValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                    }
                                    String phone = ExcelHelper.getValue(row.getCell(5)).toString();
                                    String identity_card = ExcelHelper.getValue(row.getCell(6)).toString();
                                    String gender = ExcelHelper.getValue(row.getCell(7)).toString();
                                    String course = ExcelHelper.getValue(row.getCell(8)).toString();

                                    if (!first_name.isEmpty() && !last_name.isEmpty() && !email.isEmpty() && !dob.isEmpty() && !phone.isEmpty()
                                            && !identity_card.isEmpty() && !gender.isEmpty() && !course.isEmpty()) {

                                        // Get Major
                                        ResponseEntity<ResponseModel> responseMajor = restTemplate.exchange(MAJOR_URL + "findByMajorCode/" + course, HttpMethod.GET, request, ResponseModel.class);
                                        String majorJson = new ObjectMapper().writeValueAsString(responseMajor.getBody().getData());
                                        Major major = new ObjectMapper().readValue(majorJson, Major.class);

                                        if (major != null) {
                                            // Get Profile
                                            ResponseEntity<ResponseModel> profileResponse = restTemplate.exchange(PROFILE_URL + "findByIdentityCard/" + identity_card, HttpMethod.GET, request, ResponseModel.class);
                                            String profileJson = new ObjectMapper().writeValueAsString(profileResponse.getBody().getData());
                                            Profile profile = new ObjectMapper().readValue(profileJson, Profile.class);

                                            if (profile == null) {
                                                String password = RandomStringUtils.random(8, 0, combinedChars.length(), true, true, combinedChars.toCharArray());

                                                // Generate Student Card
                                                String studentCard = com.example.smsweb.utils.StringUtils.randomStudentCard(numbers);
                                                ResponseEntity<String> response = restTemplate.exchange(STUDENT_URL + "findStudentCard/" + studentCard, HttpMethod.GET, request, String.class);
                                                if (response.getBody() != null) {
                                                    studentCard = com.example.smsweb.utils.StringUtils.randomStudentCard(numbers);
                                                }

                                                // Save Account
                                                Account account = new Account(studentCard.toLowerCase(), password, role.getId());
                                                String jsonAccount = new ObjectMapper().writeValueAsString(account);
                                                HttpHeaders headersAccount = new HttpHeaders();
                                                headersAccount.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                                                headersAccount.set("Authorization", "Bearer " + _token);
                                                MultiValueMap<String, String> paramsAccount = new LinkedMultiValueMap<>();
                                                paramsAccount.add("account", jsonAccount);
                                                HttpEntity<MultiValueMap<String, String>> requestAccount = new HttpEntity<>(paramsAccount, headersAccount);
                                                ResponseEntity<ResponseModel> responseAccount = restTemplate.exchange(ACCOUNT_URL, HttpMethod.POST, requestAccount, ResponseModel.class);
                                                String accountResponseToJson = new ObjectMapper().writeValueAsString(responseAccount.getBody().getData());
                                                Account accountResponse = new ObjectMapper().readValue(accountResponseToJson, Account.class);

                                                Profile parseProfile = new Profile();
                                                parseProfile.setDob(dob);
                                                parseProfile.setEmail(email);
                                                parseProfile.setPhone(phone);
                                                parseProfile.setIdentityCard(identity_card);
                                                parseProfile.setFirstName(first_name);
                                                parseProfile.setLastName(last_name);
                                                parseProfile.setSex(gender);

                                                //Default image
                                                File fileImage = ResourceUtils.getFile("classpath:static/img/avatar.png");
                                                FileInputStream inputStream = new FileInputStream(fileImage);
                                                MultipartFile multipartFile = new MockMultipartFile("file", fileImage.getName(), "image/png", IOUtil.toByteArray(inputStream));

                                                //save profile
                                                HttpHeaders headersProfile = new HttpHeaders();
                                                headersProfile.set("Content-Type", "multipart/form-data");
                                                headersProfile.set("Authorization", "Bearer " + _token);
                                                MultiValueMap<String, Object> paramsProfile = new LinkedMultiValueMap<>();
                                                parseProfile.setAccountId(accountResponse.getId());
                                                String jsonProfile = new ObjectMapper().writeValueAsString(parseProfile);
                                                paramsProfile.add("file", multipartFile.getResource());
                                                paramsProfile.add("profile", jsonProfile);
                                                HttpEntity<MultiValueMap<String, Object>> requestEntityProfile = new HttpEntity<>(paramsProfile, headersProfile);
                                                ResponseEntity<ResponseModel> responseProfile = restTemplate.exchange(PROFILE_URL, HttpMethod.POST, requestEntityProfile, ResponseModel.class);
                                                String profileResponseToJson = new ObjectMapper().writeValueAsString(responseProfile.getBody().getData());
                                                Profile saveProfileResponse = new ObjectMapper().readValue(profileResponseToJson, Profile.class);

//                                          // Send mail
                                                Mail mail = new Mail();
                                                mail.setToMail(parseProfile.getEmail());
                                                mail.setSubject("Account student HKT SYSTEM");
                                                String name = saveProfileResponse.getFirstName() + " " + saveProfileResponse.getLastName();
                                                Map<String, Object> props = new HashMap<>();
                                                props.put("accountName", studentCard);
                                                props.put("password", password);
                                                props.put("fullname", name);
                                                mail.setProps(props);
                                                mailService.sendHtmlMessage(mail);
                                                //-----------------------------

                                                //Save student
                                                MultiValueMap<String, String> paramsStudent = new LinkedMultiValueMap<>();
                                                paramsStudent.add("studentCard", studentCard);
                                                paramsStudent.add("profileId", String.valueOf(saveProfileResponse.getId()));
                                                HttpEntity<MultiValueMap<String, String>> requestEntityStudent = new HttpEntity<>(paramsStudent, headers);
                                                ResponseEntity<ResponseModel> responseModelStudent = restTemplate.exchange(STUDENT_URL, HttpMethod.POST, requestEntityStudent, ResponseModel.class);
                                                String studentResponseToJson = new ObjectMapper().writeValueAsString(responseModelStudent.getBody().getData());
                                                Student studentResponse = new ObjectMapper().readValue(studentResponseToJson, Student.class);
                                                //----------------------

                                                // Get Class by classId
                                                ResponseEntity<ResponseModel> responseClass = restTemplate.exchange(CLASS_URL + "getClass/" + classId, HttpMethod.GET, request, ResponseModel.class);
                                                String jsonClass = new ObjectMapper().writeValueAsString(responseClass.getBody().getData());
                                                Classses classModel = new ObjectMapper().readValue(jsonClass, Classses.class);
                                                List<StudentSubject> studentSubjectList = new ArrayList<>();

                                                //Add value to studentSubjectList
                                                for (Subject subject : classModel.getMajor().getSubjectsById()) {
                                                    MultiValueMap<String, Integer> studentSubjectContent = new LinkedMultiValueMap<>();

                                                    StudentSubject studentSubject = new StudentSubject();
                                                    studentSubject.setStudentId(studentResponse.getId());
                                                    studentSubject.setSubjectId(subject.getId());
                                                    studentSubject.setStatus("0");

                                                    studentSubjectContent.add("subjectId", subject.getId());
                                                    studentSubjectContent.add("studentId", studentResponse.getId());
                                                    HttpEntity<MultiValueMap<String, Integer>> requestStudentSubject = new HttpEntity<>(studentSubjectContent, headers);
                                                    ResponseEntity<String> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "findStudentSubjectBySubjectIdAndStudentId", HttpMethod.POST, requestStudentSubject, String.class);
                                                    ResponseModel responseModelStudentSubject = new ObjectMapper().readValue(responseStudentSubject.getBody(), new TypeReference<>() {
                                                    });

                                                    if (responseModelStudentSubject.getData() != null) {
                                                        String jsonStudentSubject = new ObjectMapper().writeValueAsString(responseModelStudentSubject.getData());
                                                        StudentSubject studentSubjectModel = new ObjectMapper().readValue(jsonStudentSubject, StudentSubject.class);
                                                        studentSubject.setId(studentSubjectModel.getId());
                                                    }

                                                    studentSubjectContent.remove("subjectId");
                                                    studentSubjectContent.remove("studentId");

                                                    studentSubjectList.add(studentSubject);
                                                }

                                                MultiValueMap<String, String> saveStudentSubjectContent = new LinkedMultiValueMap<>();
                                                saveStudentSubjectContent.add("student_subject", new ObjectMapper().writeValueAsString(studentSubjectList));
                                                HttpEntity<MultiValueMap<String, String>> studentSubjectSaveRequest = new HttpEntity<>(saveStudentSubjectContent, headers);
                                                ResponseEntity<ResponseModel> responseStudentSubject = restTemplate.exchange(STUDENT_SUBJECT_URL + "updateAll", HttpMethod.POST, studentSubjectSaveRequest, ResponseModel.class);


                                                // -----------------------------------

                                                // Save student class
                                                StudentClass studentClass = new StudentClass();
                                                studentClass.setClassId(classId);
                                                studentClass.setStudentId(studentResponse.getId());
                                                String studentClassJson = new ObjectMapper().writeValueAsString(studentClass);
                                                MultiValueMap<String, String> studentClassContent = new LinkedMultiValueMap<>();
                                                studentClassContent.add("newStudentClass", studentClassJson);
                                                HttpEntity<MultiValueMap<String, String>> studentClassRequest = new HttpEntity<>(studentClassContent, headers);
                                                restTemplate.exchange(STUDENT_CLASS_URL + "save", HttpMethod.POST, studentClassRequest, ResponseModel.class);

                                                //----------------------

                                                // Save major student
                                                MajorStudent majorStudent = new MajorStudent(major.getId(), studentResponse.getId());
                                                String jsonMajorStudent = new ObjectMapper().writeValueAsString(majorStudent);
                                                HttpHeaders headersMajorStudent = new HttpHeaders();
                                                headersMajorStudent.set("Content-Type", "multipart/form-data");
                                                headersMajorStudent.set("Authorization", "Bearer " + _token);
                                                MultiValueMap<String, String> paramsMajorStudent = new LinkedMultiValueMap<>();
                                                paramsMajorStudent.add("student_major", jsonMajorStudent);
                                                HttpEntity<MultiValueMap<String, String>> requestEntityMajorStudent = new HttpEntity<>(paramsMajorStudent, headersMajorStudent);
                                                restTemplate.exchange(STUDENT_MAJOR_URL, HttpMethod.POST, requestEntityMajorStudent, ResponseModel.class);
                                                //---------
                                            } else {
                                                ResponseEntity<Student> responseStudent = restTemplate.exchange(STUDENT_URL + "getByProfile/" + profile.getId(), HttpMethod.GET, request, Student.class);

                                                MultiValueMap<String, Integer> studentClassContent = new LinkedMultiValueMap<>();
                                                studentClassContent.add("classId", classId);
                                                studentClassContent.add("studentId", responseStudent.getBody().getId());
                                                HttpEntity<MultiValueMap<String, Integer>> studentClassRequest = new HttpEntity<>(studentClassContent, headers);
                                                ResponseEntity<ResponseModel> responseStudentClass = restTemplate.exchange(STUDENT_CLASS_URL + "getStudentClassByClassIdAndStudentId", HttpMethod.POST, studentClassRequest, ResponseModel.class);
                                                String studentClassJson = new ObjectMapper().writeValueAsString(responseStudentClass.getBody().getData());
                                                StudentClass studentClassResponse = new ObjectMapper().readValue(studentClassJson, StudentClass.class);
                                                if (studentClassResponse == null) {
                                                    StudentClass studentInClass = new StudentClass();
                                                    studentInClass.setClassId(classId);
                                                    studentInClass.setStudentId(responseStudent.getBody().getId());
                                                    listStudentClass.add(studentInClass);
                                                    flag = true;
                                                } else {
                                                    return new ResponseEntity<String>("Student at row " + (rowIndex + 1) + " have in this class", HttpStatus.BAD_REQUEST);
                                                }
                                            }
                                        } else {
                                            return new ResponseEntity<String>("Can't find curriculum at row " + (rowIndex + 1), HttpStatus.BAD_REQUEST);
                                        }
                                    } else {
                                        return new ResponseEntity<String>("Some field at row " + (rowIndex + 1) + " is empty please fill it", HttpStatus.BAD_REQUEST);
                                    }
                                }
                                if (!flag) {
                                    if (!listStudentClass.isEmpty()) {
                                        MultiValueMap<String, String> studentClassContent = new LinkedMultiValueMap<>();
                                        studentClassContent.add("listStudentClass", new ObjectMapper().writeValueAsString(listStudentClass));
                                        HttpEntity<MultiValueMap<String, String>> studentClassRequest = new HttpEntity<>(studentClassContent, headers);
                                        restTemplate.exchange(STUDENT_CLASS_URL + "saveAll", HttpMethod.POST, studentClassRequest, ResponseModel.class);
                                        return new ResponseEntity<String>("Success", HttpStatus.OK);
                                    } else {
                                        return new ResponseEntity<String>("All student was have in this class", HttpStatus.BAD_REQUEST);
                                    }
                                } else {
                                    return new ResponseEntity<String>("Success", HttpStatus.OK);
                                }
                            } else {
                                return new ResponseEntity<String>("Excel data must less than limit student in class", HttpStatus.BAD_REQUEST);
                            }
                        } catch (Exception e) {
                            log.error("Import Student: " + e.getMessage());
                            return new ResponseEntity<String>("Import excel fail", HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        return new ResponseEntity<String>("Please choose excel file", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<String>("Please choose file", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>(isExpired, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<String>("Can't import student", HttpStatus.NOT_FOUND);
        }
    }
}
