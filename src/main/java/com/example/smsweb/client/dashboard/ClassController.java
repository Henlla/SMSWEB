package com.example.smsweb.client.dashboard;


import com.example.smsweb.dto.*;
import com.example.smsweb.jwt.JWTUtils;
import com.example.smsweb.models.*;
import com.example.smsweb.utils.DayOfWeekSchedule;
import com.example.smsweb.utils.ExcelHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/dashboard/class")
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


    @GetMapping("/class-index")
    public String index(Model model, @CookieValue(name = "_token", defaultValue = "") String _token) throws JsonProcessingException {
        if (_token.equals("")) {
            return "redirect:/dashboard/login";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + _token);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(CLASS_URL + "list", HttpMethod.GET, request, String.class);
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
                        Boolean addStudent = UpdateClassIdForStudentClass(_token, file, classModel.getClassCode());
                        if (addStudent) {
                            return new responModelForClass("success", "Thêm danh sách sinh viên thành công").toString();
                        } else {
                            return new responModelForClass("success", "Thêm danh sách sinh viên thất bại").toString();
                        }
                    } catch (Exception e) {
                        String message = StringUtils.substringBetween(e.getMessage(), "\"", "\"");
                        return new responModelForClass("success", "Thêm danh sách sinh viên thất bại. " + message).toString();
                    }
                } else {
                    return new responModelForClass("success", "").toString();
                }
            } else {
                return new responModelForClass("fail", "").toString();
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

    private Boolean UpdateClassIdForStudentClass(String _token, MultipartFile file, String classCode) throws JsonProcessingException {
        if (!file.isEmpty()) {
            try {
                // get list studentId form Excel file
                List<String> listStudentCard = new ArrayList<>();
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet sheet = workbook.getSheetAt(0);
                for (int rowIndex = 0; rowIndex < ExcelHelper.getNumberOfNonEmptyCells(sheet, 0); rowIndex++) {
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

                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                var s = e.getMessage();
                throw new RuntimeException(e.getMessage());
            }
        }
        return false;
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
            model.addAttribute("class", classses);
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
                        for (int i = 0; i < subject.getSlot(); i++) {
                            if (i != 1) {
                                String dayOfWeek = String.valueOf(date.getDayOfWeek());
                                int getDate = date.getDayOfMonth();
                                int getMonth = date.getMonthValue();
                                boolean isInHoliday = holidayList.stream().noneMatch(holiday -> holiday.getDate().getMonthValue() == getMonth && holiday.getDate().getDayOfMonth() == getDate);
                                if (Arrays.stream(DayOfWeekSchedule.array0).anyMatch(dayOfWeek::equals)
                                        && isInHoliday) {

                                    ScheduleDetail scheduleDetail = new ScheduleDetail();
                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                    scheduleDetail.setDate(date.toString());
                                    scheduleDetail.setScheduleId(schedule.getId());
                                    scheduleDetail.setSubjectId(subject.getId());
                                    listScheduleDetails.add(scheduleDetail);
                                    slot++;
                                }
                                break;
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
                        for (int i = 0; i < subject.getSlot(); i++) {
                            if (i != 1) {
                                String dayOfWeek = String.valueOf(date.getDayOfWeek());
                                int getDate = date.getDayOfMonth();
                                int getMonth = date.getMonthValue();
                                boolean isInHoliday = holidayList.stream().noneMatch(holiday -> holiday.getDate().getMonthValue() == getMonth && holiday.getDate().getDayOfMonth() == getDate);
                                if (Arrays.stream(DayOfWeekSchedule.array1).anyMatch(dayOfWeek::equals)
                                        && isInHoliday) {

                                    ScheduleDetail scheduleDetail = new ScheduleDetail();
                                    scheduleDetail.setDayOfWeek(dayOfWeek);
                                    scheduleDetail.setDate(date.toString());
                                    scheduleDetail.setScheduleId(schedule.getId());
                                    scheduleDetail.setSubjectId(subject.getId());
                                    listScheduleDetails.add(scheduleDetail);
                                    slot++;
                                }
                                break;
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
                for (ScheduleDetail scheduleDetail : schedule.getScheduleDetailsById()) {
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int weekOfMonth = LocalDate.parse(scheduleDetail.getDate()).get(weekFields.weekOfMonth());
                    int monthOfYear = LocalDate.parse(scheduleDetail.getDate()).getMonthValue();
                    int weekOfYear = LocalDate.parse(scheduleDetail.getDate()).get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                    DayInWeek diw = new DayInWeek();
                    diw.setDate(LocalDate.parse(scheduleDetail.getDate()));
                    diw.setSubject(scheduleDetail.getSubjectBySubjectId());
                    diw.setDayOfWeek(scheduleDetail.getDayOfWeek());
                    diw.setSubjectId(scheduleDetail.getSubjectId());
                    diw.setWeek(weekOfMonth);
                    diw.setMonth(monthOfYear);
                    diw.setWeekOfYear(weekOfYear);
                    dayInWeekList.add(diw);
                }

                scheduleModel.setStartDate(LocalDate.parse(schedule.getStartDate()));
                scheduleModel.setId(scheduleModel.getId());
                scheduleModel.setEndDate(LocalDate.parse(schedule.getEndDate()));
                scheduleModel.setSemester(schedule.getSemester());
                scheduleModel.setDayInWeeks(dayInWeekList);
                return scheduleModel;
            }else{
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

    class responModelForClass {
        String status;
        String message;

        public responModelForClass(String status, String message) {
            this.status = status;
            this.message = message;
        }

        @Override
        public String toString() {
            return "{\"status\":\"" + status + "\"," +
                    "\"message\":\"" + message + "\"" +
                    "}";
        }
    }
}
