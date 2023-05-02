package com.interswitchgroup.tx_user_portal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.interswitchgroup.tx_user_portal.entities.AuditLog;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ExportService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

    @Async
    public ByteArrayInputStream convertToExcel(List<AuditLog> logs){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Portal Logs");
            int rowNum = 0;
            int columnCount = 0;

            // Create the header row dynamically based on the fields in the data
            Row headerRow = sheet.createRow(rowNum++);
            Map<String, Object> firstDataRow = objectMapper.convertValue(logs.get(0), Map.class);
            for (Map.Entry<String, Object> entry : firstDataRow.entrySet()) {
                headerRow.createCell(columnCount).setCellValue(entry.getKey());
                columnCount++;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (AuditLog d : logs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(d.getLogId());
                row.createCell(1).setCellValue(d.getLogActivity().name());
                row.createCell(2).setCellValue(d.getExecutedBy());
                row.createCell(3).setCellValue(d.getDetails());
                row.createCell(4).setCellValue(d.getDateCreated().format(formatter));

            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return new ByteArrayInputStream(bos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Error creating Excel file", e);
        }
    }
}
