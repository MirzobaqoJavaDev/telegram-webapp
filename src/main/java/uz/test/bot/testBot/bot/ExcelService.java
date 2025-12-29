package uz.test.bot.testBot.bot;

import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Service
public class ExcelService {

    private static final String FILE_PREFIX = "class_";
    private static final String FILE_SUFFIX = ".xlsx";

    // 🔥 RUN bo‘lganda barcha Excel fayllarni o‘chirish
    @PostConstruct
    public void deleteAllExcelFiles() {
        for (int i = 1; i <= 11; i++) {
            File file = new File(FILE_PREFIX + i + FILE_SUFFIX);
            if (file.exists()) {
                file.delete();
                System.out.println(file.getName() + " DELETED");
            }
        }
    }

    public void saveToExcel(
            String phone,
            String firstName,
            String lastName,
            int classNumber
    ) {

        try {
            String fileName = FILE_PREFIX + classNumber + FILE_SUFFIX;
            File file = new File(fileName);

            XSSFWorkbook workbook;
            XSSFSheet sheet;

            if (!file.exists()) {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Class " + classNumber);

                // Header
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Phone");
                header.createCell(1).setCellValue("First Name");
                header.createCell(2).setCellValue("Last Name");
                header.createCell(3).setCellValue("Class");

            } else {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
                fis.close();
            }

            int rowCount = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(rowCount);

            row.createCell(0).setCellValue(phone);
            row.createCell(1).setCellValue(firstName);
            row.createCell(2).setCellValue(lastName);
            row.createCell(3).setCellValue(classNumber);

            // 🔹 Ustunlarni avtomatik kengaytirish
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fos = new FileOutputStream(fileName);
            workbook.write(fos);
            fos.close();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//    public void saveToExcel(String name, String surname,String number){
//
//        try {
//            File file = new File(FILE_PATH);
//            XSSFWorkbook workbook;
//            XSSFSheet sheet;
//
//            if (!file.exists()){
//                workbook = new XSSFWorkbook();
//                sheet = workbook.createSheet("Users");
//
//                Row header = sheet.createRow(0);
//                header.createCell(0).setCellValue("Name");
//                header.createCell(1).setCellValue("Surname");
//            }else {
//                FileInputStream fis = new FileInputStream(file);
//                workbook = new XSSFWorkbook(fis);
//                sheet = workbook.getSheetAt(0);
//            }
//            int rowCount = sheet.getLastRowNum()+1;
//            Row row = sheet.createRow(rowCount);
//            row.createCell(0).setCellValue(name);
//            row.createCell(1).setCellValue(surname);
//
//            for (int i = 0; i < row.getLastCellNum(); i++) {
//                sheet.autoSizeColumn(i);
//            }
//
//            FileOutputStream fos = new FileOutputStream(FILE_PATH);
//            workbook.write(fos);
//            fos.close();
//            workbook.close();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }


