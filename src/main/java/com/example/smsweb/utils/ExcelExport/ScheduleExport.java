package com.example.smsweb.utils.ExcelExport;

import com.example.smsweb.dto.DayInWeek;
import com.example.smsweb.dto.ScheduleModel;
import com.example.smsweb.models.Classses;
import com.example.smsweb.models.Student;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.poi.ss.util.CellUtil.createCell;

public class ScheduleExport {
    ScheduleModel scheduleModel;
    private XSSFWorkbook workbook;
    Classses classses;
    private XSSFSheet sheet;

    public ScheduleExport(ScheduleModel scheduleModel, Classses classses) {
        this.scheduleModel = scheduleModel;
        this.classses = classses;
        workbook = new XSSFWorkbook();
    }

    private void writeHeader(){
        sheet = workbook.createSheet("Timetable");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
        font.setBold(true);
        font.setFontHeight(22);
        font.setItalic(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        createCell(row, 0, "THỜI KHÓA BIỂU LỚP "+classses.getClassCode()+" Kì "+scheduleModel.getSemester(), style);

        sheet.addMergedRegion(new CellRangeAddress(1,1,0,6));
        row = sheet.createRow(1);
        font = workbook.createFont();
        style = workbook.createCellStyle();
        font.setBold(true);
        font.setFontHeight(10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        createCell(row, 0, "Ngày bắt đầu "+scheduleModel.getStartDate(), style);

        sheet.addMergedRegion(new CellRangeAddress(2,2,0,6));
        row = sheet.createRow(2);
        font = workbook.createFont();
        style = workbook.createCellStyle();
        font.setBold(true);
        font.setFontHeight(10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        createCell(row, 0, "Ngày kết thúc "+scheduleModel.getEndDate(), style);

        row = sheet.createRow(3);
        font = workbook.createFont();
        style = workbook.createCellStyle();
        font.setFontHeight(12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.AQUA.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        createCell(row,0,"Thứ hai",style);
        createCell(row,1,"Thứ ba",style);
        createCell(row,2,"Thứ tư",style);
        createCell(row,3,"Thứ năm",style);
        createCell(row,4,"Thứ sáu",style);
        createCell(row,5,"Thứ bảy",style);
        createCell(row,6,"Chủ nhật",style);

    }

    private void createCell(Row row, int columnCount, Object valueOfCell,CellStyle cellStyle){
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        CellStyle style = workbook.createCellStyle();
//        CreationHelper createHelper = workbook.getCreationHelper();
//        style.setDataFormat(
//                createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
        if(valueOfCell instanceof  Integer){
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        }else if (valueOfCell instanceof Date) {
            cell.setCellValue((Date) valueOfCell);
        }else if (valueOfCell instanceof Double) {
            cell.setCellValue((Double) valueOfCell);
        }else {
            cell.setCellValue((Boolean) valueOfCell);
        }
        cell.setCellStyle(cellStyle);
    }

    private void write(){
        int rowCount =4;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        HashMap<Integer, List<DayInWeek>> hashMap = new HashMap<Integer, List<DayInWeek>>();
        List<DayInWeek> listSort = scheduleModel.getDayInWeeks().stream().sorted((a,b)-> a.getWeekOfYear().compareTo(b.getWeekOfYear())).toList();
        for (DayInWeek dayInWeek:listSort){
            Integer key  = dayInWeek.getWeekOfYear();
            if(hashMap.containsKey(key)){
                List<DayInWeek> list = hashMap.get(key);
                list.add(dayInWeek);

            }else{
                List<DayInWeek> list = new ArrayList<DayInWeek>();
                list.add(dayInWeek);
                hashMap.put(key, list);
            }
        }
        HashMap<Integer, List<DayInWeek>> newMapSortedByKey = hashMap.entrySet().stream()
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .collect(Collectors.toMap(HashMap.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        for(Map.Entry<Integer, List<DayInWeek>> entry : newMapSortedByKey.entrySet()) {
            Integer key = entry.getKey();
            List<DayInWeek> value = entry.getValue();
            Row row = sheet.createRow(rowCount++);
            style = workbook.createCellStyle();
            font = workbook.createFont();
            font.setFontHeight(13);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            createCell(row,0,"##",style);
            createCell(row,1,"##",style);
            createCell(row,2,"##",style);
            createCell(row,3,"##",style);
            createCell(row,4,"##",style);
            createCell(row,5,"##",style);
            createCell(row,6,"##",style);
            for (DayInWeek dayInWeek :value){
                if(dayInWeek.getDayOfWeek().equals("MONDAY")){
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setWrapText(true);
                    createCell(row,0,dayInWeek.getSubject().getSubjectCode()+"\n"+dayInWeek.getDate().toString(),style);
                }else if(dayInWeek.getDayOfWeek().equals("TUESDAY")){
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setWrapText(true);
                    createCell(row,1,dayInWeek.getSubject().getSubjectCode()+"\n"+dayInWeek.getDate().toString(),style);
                }else if(dayInWeek.getDayOfWeek().equals("WEDNESDAY")){
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setWrapText(true);
                    createCell(row,2,dayInWeek.getSubject().getSubjectCode()+"\n"+dayInWeek.getDate().toString(),style);
                }else if(dayInWeek.getDayOfWeek().equals("THURSDAY")){
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setWrapText(true);
                    createCell(row,3,dayInWeek.getSubject().getSubjectCode()+"\n"+dayInWeek.getDate().toString(),style);
                }else if (dayInWeek.getDayOfWeek().equals("FRIDAY")){
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setWrapText(true);
                    createCell(row,4,dayInWeek.getSubject().getSubjectCode()+"\n"+dayInWeek.getDate().toString(),style);
                }else if(dayInWeek.getDayOfWeek().equals("SATURDAY")){
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setWrapText(true);
                    createCell(row,5,dayInWeek.getSubject().getSubjectCode()+"\n"+dayInWeek.getDate().toString(),style);
                } else if (dayInWeek.getDayOfWeek().equals("SUNDAY")) {
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    style.setWrapText(true);
                    createCell(row,6,dayInWeek.getSubject().getSubjectCode()+"\n"+dayInWeek.getDate().toString(),style);
                } else if (dayInWeek.getDayOfWeek().equals("1")) {
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    createCell(row,0,"##",style);
                }else if (dayInWeek.getDayOfWeek().equals("2")) {
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    createCell(row,1,"##",style);
                }else if (dayInWeek.getDayOfWeek().equals("3")) {
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    createCell(row,2,"##",style);
                }else if (dayInWeek.getDayOfWeek().equals("4")) {
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    createCell(row,3,"##",style);
                }else if (dayInWeek.getDayOfWeek().equals("5")) {
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    createCell(row,4,"##",style);
                }else if (dayInWeek.getDayOfWeek().equals("6")) {
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    createCell(row,5,"##",style);
                }else if (dayInWeek.getDayOfWeek().equals("7")) {
                    style = workbook.createCellStyle();
                    font = workbook.createFont();
                    font.setFontHeight(13);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style.setBorderBottom(BorderStyle.THIN);
                    style.setBorderTop(BorderStyle.THIN);
                    style.setBorderLeft(BorderStyle.THIN);
                    style.setBorderRight(BorderStyle.THIN);
                    createCell(row,6,"##",style);
                }
            }
        }
    }

    public void generateExcelFile(HttpServletResponse response) throws IOException {
        writeHeader();
        write();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
