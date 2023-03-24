package com.example.smsweb.utils.ExcelExport;

import com.example.smsweb.models.Classses;
import com.example.smsweb.models.StudentClass;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.Date;

public class ClassExport {
    Classses classModel;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    Row row;
    CellStyle style;
    XSSFFont font;

    public ClassExport(Classses classModel) {
        this.classModel = classModel;
        workbook = new XSSFWorkbook();
    }
    private void writeHeader(){
        sheet = workbook.createSheet("CLASS "+ classModel.getClassCode());
        row = sheet.createRow(1);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0,"STT",style);
        createCell(row, 1, "Mã sinnh viên", style);
        createCell(row, 2, "Họ và tên", style);
        createCell(row, 3, "Giới tính", style);
        createCell(row, 4, "Ngày sinh", style);
        createCell(row, 5, "Số điện thoại", style);
        createCell(row, 6, "Email", style);
        createCell(row, 7, "Tỉnh", style);
    }

    //Create Title
    private void createTitle(){
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,6));
        row = sheet.createRow(0);
        style = workbook.createCellStyle();
        font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(20);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        createCell(row, 0, "STUDENTS OF CLASS "+classModel.getClassCode(), style);
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

    private void writeData(){
        style = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        int rowCount = 2;
        int rowIndex = 1;
        for (StudentClass studentClass:classModel.getStudentClassById()) {
            row = sheet.createRow(rowCount);
            createCell(row, 0, rowIndex, style);
            createCell(row, 1, studentClass.getClassStudentByStudent().getStudentCard(), style);
            createCell(row, 2,studentClass.getClassStudentByStudent().getStudentByProfile().getFirstName()+ " "
                            + studentClass.getClassStudentByStudent().getStudentByProfile().getLastName(),
                    style);
            createCell(row, 3, studentClass.getClassStudentByStudent().getStudentByProfile().getSex(), style);
            createCell(row, 4, studentClass.getClassStudentByStudent().getStudentByProfile().getDob(), style);
            createCell(row, 5, studentClass.getClassStudentByStudent().getStudentByProfile().getPhone(), style);
            createCell(row, 6, studentClass.getClassStudentByStudent().getStudentByProfile().getEmail(), style);
            createCell(row, 7, studentClass.getClassStudentByStudent().getStudentByProfile().getProfileProvince().getName(), style);
            rowCount++;
            rowIndex++;
        }
    }

    public void generateExcelFile(HttpServletResponse response) throws IOException {
        createTitle();
        writeHeader();
        writeData();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
