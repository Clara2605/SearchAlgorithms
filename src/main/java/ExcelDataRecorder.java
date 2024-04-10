import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ExcelDataRecorder {

    private static String directoryPath = "src/main/resources/data_output/";

    // Metodă generică pentru scrierea datelor, fie că e vorba de timp de execuție sau utilizare memorie
    public static void writeData(String baseFileName, List<Double> bfsData, List<Double> dfsData, int nodeCount, boolean isExecutionTime) throws IOException {
        // Ajustează calea și numele fișierului în funcție de tipul datelor
        String fileNameModifier = isExecutionTime ? "ExecutionTimes" : "MemoryUsage";
        String filePath = directoryPath + baseFileName.replace("ExecutionTimes", fileNameModifier);

        Files.createDirectories(Paths.get(directoryPath)); // Asigură-te că directorul există
        Workbook workbook;
        Sheet sheet;

        // Verifică dacă fișierul există
        if (Files.exists(Paths.get(filePath))) {
            try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
                workbook = WorkbookFactory.create(is);
            }
        } else {
            workbook = new XSSFWorkbook();
        }

        sheet = getSheet(workbook, isExecutionTime);

        // Logică similară pentru găsirea următoarei rânduri libere și scrierea datelor
        int columnOffset = nodeCount == 10000 ? 0 : 2;
        int rowNumberToWrite = getNextEmptyRow(sheet, columnOffset);
        Row row = sheet.getRow(rowNumberToWrite);
        if (row == null) {
            row = sheet.createRow(rowNumberToWrite);
        }

        writeDataInColumns(bfsData, dfsData, columnOffset, row);

        // Scrie workbook-ul în fișier
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    private static Sheet getSheet(Workbook workbook, boolean isExecutionTime) {
        String sheetName = isExecutionTime ? "Execution Times" : "Memory Usage";
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
            initializeHeaderRow(sheet, isExecutionTime);
        }
        return sheet;
    }

    private static void initializeHeaderRow(Sheet sheet, boolean isExecutionTime) {
        Row headerRow = sheet.createRow(0);
        if (isExecutionTime) {
            headerRow.createCell(0).setCellValue("graphBFS Execution Time 10000 (s)");
            headerRow.createCell(1).setCellValue("graphDFS Execution Time 10000 (s)");
            headerRow.createCell(2).setCellValue("graphBFS Execution Time 1000 (s)");
            headerRow.createCell(3).setCellValue("graphDFS Execution Time 1000 (s)");
        } else {
            headerRow.createCell(0).setCellValue("graphBFS Memory Usage 10000 (bytes)");
            headerRow.createCell(1).setCellValue("graphDFS Memory Usage 10000 (bytes)");
            headerRow.createCell(2).setCellValue("graphBFS Memory Usage 1000 (bytes)");
            headerRow.createCell(3).setCellValue("graphDFS Memory Usage 1000 (bytes)");
        }
    }

    private static void writeDataInColumns(List<Double> bfsData, List<Double> dfsData, int columnOffset, Row row) {
        Cell cell;
        if (!bfsData.isEmpty()) {
            cell = row.createCell(columnOffset, CellType.NUMERIC);
            cell.setCellValue(bfsData.get(0)); // Presupunem o singură valoare per execuție
        }
        if (!dfsData.isEmpty()) {
            cell = row.createCell(columnOffset + 1, CellType.NUMERIC);
            cell.setCellValue(dfsData.get(0)); // Presupunem o singură valoare per execuție
        }
    }

    private static int getNextEmptyRow(Sheet sheet, int columnIndex) {
        int rowNum;
        for (rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) { // Start de la 1 pentru a evita header-ul
            Row row = sheet.getRow(rowNum);
            if (row == null || row.getCell(columnIndex) == null || row.getCell(columnIndex).toString().trim().isEmpty()) {
                break;
            }
        }
        return rowNum;
    }
}
