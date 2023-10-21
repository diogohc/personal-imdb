package MyImdb.demo.utils;

import MyImdb.demo.model.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class ExcelUser {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private List<User> listUsers;


    public ExcelUser(List<User> listUsers){
        this.listUsers = listUsers;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Users");
    }

    private void writeHeaderRow(){
        int columnCount = 0;
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        Cell cell = row.createCell(columnCount++);
        cell.setCellValue("ID");
        cell.setCellStyle(style);

        cell = row.createCell(columnCount++);
        cell.setCellValue("Username");
        cell.setCellStyle(style);

        cell = row.createCell(columnCount++);
        cell.setCellValue("Role");
        cell.setCellStyle(style);
    }

    private void writeDataRows(){
        int rowCount = 1;
        int columnCount;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for(User user: listUsers){
            columnCount=0;
            Row row = sheet.createRow(rowCount++);

            Cell cell = row.createCell(columnCount);
            cell.setCellValue(user.getId());
            sheet.autoSizeColumn(columnCount);
            columnCount++;

            cell = row.createCell(columnCount);
            cell.setCellValue(user.getUsername());
            sheet.autoSizeColumn(columnCount);
            columnCount++;

            cell = row.createCell(columnCount);
            cell.setCellValue(user.getRole().toString());
            sheet.autoSizeColumn(columnCount);
            columnCount++;
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderRow();
        writeDataRows();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }
}
