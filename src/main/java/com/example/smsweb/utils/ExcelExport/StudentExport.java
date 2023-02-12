package com.example.smsweb.utils.ExcelExport;

import com.example.smsweb.models.MajorStudent;
import com.example.smsweb.models.Student;
import com.example.smsweb.models.StudentClass;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StudentExport {
    List<Student> studentList;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public StudentExport(List<Student> studentList) {
        this.studentList = studentList;
        workbook = new XSSFWorkbook();
    }

    private void writeHeader(){
        sheet = workbook.createSheet("FeedBack");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row,0,"STT",style);
        createCell(row,1,"Mã sinh viên",style);
        createCell(row,2,"Họ",style);
        createCell(row,3,"Tên",style);
        createCell(row,4,"Họ và tên",style);
        createCell(row,5,"Email",style);
        createCell(row,6,"Phone",style);
        createCell(row,7,"Giới tính",style);
        createCell(row,8,"Địa chỉ",style);
        createCell(row,9,"Nghành học",style);
        createCell(row,10,"Lớp học",style);
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
        int rowCount =1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(13);
        style.setFont(font);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(Student student : studentList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            StringBuilder majorCurrent = new StringBuilder();
            StringBuilder classCurrent = new StringBuilder();
            createCell(row,columnCount++,studentList.indexOf(student)+1,style);
            createCell(row,columnCount++,student.getStudentCard(),style);
            createCell(row,columnCount++,student.getStudentByProfile().getFirstName(),style);
            createCell(row,columnCount++,student.getStudentByProfile().getLastName(),style);
            createCell(row,columnCount++,student.getStudentByProfile().getFirstName()+" "+student.getStudentByProfile().getLastName(),style);
            createCell(row,columnCount++,student.getStudentByProfile().getEmail(),style);
            createCell(row,columnCount++,student.getStudentByProfile().getPhone(),style);
            createCell(row,columnCount++,student.getStudentByProfile().getSex(),style);
            createCell(row,columnCount++,student.getStudentByProfile().getAddress()+", "+student.getStudentByProfile().getWardByWardId().getName() +", "+student.getStudentByProfile().getDistrictByDistrictId().getName() + ", "+student.getStudentByProfile().getProfileProvince().getName(),style);
            if(student.getMajorStudentsById().isEmpty()){
                majorCurrent.append("Chưa có");
                createCell(row,columnCount++, majorCurrent.toString(),style);
            }else{
                for (MajorStudent majorStudent : student.getMajorStudentsById()){
                    if(student.getMajorStudentsById().get(student.getMajorStudentsById().size() - 1)!=null){
                        majorCurrent.append(majorStudent.getMajorByMajorId().getMajorName());
                    }else{
                        majorCurrent.append(majorStudent.getMajorByMajorId().getMajorName()).append(",");
                    }

                }
                createCell(row,columnCount++, majorCurrent.toString(),style);
            }

            if(student.getStudentClassById().isEmpty()){
                classCurrent.append("Chưa có");
                createCell(row,columnCount++, classCurrent.toString(),style);
            }else{
                for (StudentClass studentClass : student.getStudentClassById()){
                    if(student.getStudentClassById().get(student.getStudentClassById().size() - 1)!=null){
                        classCurrent.append(studentClass.getClassStudentByClass().getClassCode());
                    }else{
                        classCurrent.append(studentClass.getClassStudentByClass().getClassCode()).append(",");
                    }
                }
                createCell(row,columnCount++, classCurrent.toString(),style);
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