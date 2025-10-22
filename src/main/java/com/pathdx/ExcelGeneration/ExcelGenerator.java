package com.pathdx.ExcelGeneration;

import com.pathdx.dto.responseDto.AuditLogDto;
import com.pathdx.dto.responseDto.AuditLogRespDto;
import com.pathdx.dto.responseDto.AuditLogResponseDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ExcelGenerator {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<AuditLogRespDto> auditLogs;

    public ExcelGenerator(List<AuditLogRespDto> auditLogs) {
        this.auditLogs = auditLogs;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("AuditLogs");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
       // XSSFFont font = workbook.createFont();
//        font.setBold(true);
//        font.setFontHeight(14);
//        style.setFont(font);

        createCell(row, 0, "Timestamp", style);
        createCell(row, 1, "Name", style);
        createCell(row, 2, "Email Id", style);
        createCell(row, 3, "Accession Id", style);
        createCell(row, 4, "Case Status", style);
        createCell(row, 5, "Action Type", style);
        createCell(row, 6, "Description", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if(value instanceof Date){
            CreationHelper createHelper = workbook.getCreationHelper();
            style.setDataFormat(
                    createHelper.createDataFormat().getFormat("yyyy/mm/dd h:mm"));
            cell.setCellValue((Date)value );
            cell.setCellStyle(style);
        }
        else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);

        for (AuditLogRespDto auditlog : auditLogs) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, auditlog.getDateAndTime(),style);
            createCell(row, columnCount++, auditlog.getFirstName().concat(" ").concat(auditlog.getLastName()),style);
            createCell(row, columnCount++, auditlog.getEmailId(), style);
//            createCell(row, columnCount++, auditlog.get, style);
            createCell(row, columnCount++, auditlog.getAccessionId(), style);
            createCell(row, columnCount++, auditlog.getCaseStatus(), style);
            createCell(row, columnCount++, auditlog.getActionType(), style);
            createCell(row, columnCount++, auditlog.getDescription(), style);

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
