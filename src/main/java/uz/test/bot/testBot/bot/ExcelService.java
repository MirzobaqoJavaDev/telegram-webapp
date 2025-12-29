package uz.test.bot.testBot.bot;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
@Service
public class ExcelService {

    public Map<Long, String> tempNames = new HashMap<>();
    private static final String FILE_PATH = "users.xlsx";

    public void saveToExcel(String name, String surname){

        try {
            File file = new File(FILE_PATH);
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            if (!file.exists()){
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Users");

                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Name");
                header.createCell(1).setCellValue("Surname");
            }else {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
            }
            int rowCount = sheet.getLastRowNum()+1;
            Row row = sheet.createRow(rowCount);
            row.createCell(0).setCellValue(name);
            row.createCell(1).setCellValue(surname);

            for (int i = 0; i < row.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            FileOutputStream fos = new FileOutputStream(FILE_PATH);
            workbook.write(fos);
            fos.close();
            workbook.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
