import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ExcelDataRecorder {

    private static String directoryPath = "src/main/resources/data_output/";

    public static void writeData(String baseFileName, List<Double> bfsData, List<Double> dfsData, int nodeCount, boolean isExecutionTime, boolean isTree) throws IOException {
        String fileNameModifier = isExecutionTime ? "ExecutionTimes" : "MemoryUsage";
        String filePath = directoryPath + baseFileName.replace("ExecutionTimes", fileNameModifier);

        Files.createDirectories(Paths.get(directoryPath));
        Workbook workbook;
        Sheet sheet;

        if (Files.exists(Paths.get(filePath))) {
            try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
                workbook = WorkbookFactory.create(is);
            }
        } else {
            workbook = new XSSFWorkbook();
        }

        sheet = getSheet(workbook, isExecutionTime);
        int columnOffset = (isTree ? (nodeCount == 10000 ? 4 : 6) : (nodeCount == 10000 ? 0 : 2));
        int rowNumberToWrite = getNextEmptyRow(sheet, columnOffset);
        Row row = sheet.getRow(rowNumberToWrite);
        if (row == null) {
            row = sheet.createRow(rowNumberToWrite);
        }

        writeDataInColumns(bfsData, dfsData, columnOffset, row);

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
            headerRow.createCell(0).setCellValue("Graph BFS Execution Time 10000 (s)");
            headerRow.createCell(1).setCellValue("Graph DFS Execution Time 10000 (s)");
            headerRow.createCell(2).setCellValue("Graph BFS Execution Time 1000 (s)");
            headerRow.createCell(3).setCellValue("Graph DFS Execution Time 1000 (s)");
            headerRow.createCell(4).setCellValue("Tree BFS Execution Time 10000 (s)");
            headerRow.createCell(5).setCellValue("Tree DFS Execution Time 10000 (s)");
            headerRow.createCell(6).setCellValue("Tree BFS Execution Time 1000 (s)");
            headerRow.createCell(7).setCellValue("Tree DFS Execution Time 1000 (s)");
        } else {
            headerRow.createCell(0).setCellValue("Graph BFS Memory Usage 10000 (bytes)");
            headerRow.createCell(1).setCellValue("Graph DFS Memory Usage 10000 (bytes)");
            headerRow.createCell(2).setCellValue("Graph BFS Memory Usage 1000 (bytes)");
            headerRow.createCell(3).setCellValue("Graph DFS Memory Usage 1000 (bytes)");
            headerRow.createCell(4).setCellValue("Tree BFS Memory Usage 10000 (bytes)");
            headerRow.createCell(5).setCellValue("Tree DFS Memory Usage 10000 (bytes)");
            headerRow.createCell(6).setCellValue("Tree BFS Memory Usage 1000 (bytes)");
            headerRow.createCell(7).setCellValue("Tree DFS Memory Usage 1000 (bytes)");
        }
    }

    private static void writeDataInColumns(List<Double> bfsData, List<Double> dfsData, int columnOffset, Row row) {
        Cell cell;
        if (!bfsData.isEmpty()) {
            cell = row.createCell(columnOffset, CellType.NUMERIC);
            cell.setCellValue(bfsData.get(0));
        }
        if (!dfsData.isEmpty()) {
            cell = row.createCell(columnOffset + 1, CellType.NUMERIC);
            cell.setCellValue(dfsData.get(0));
        }
    }

    private static int getNextEmptyRow(Sheet sheet, int startColumnIndex) {
        int lastRowNum = sheet.getLastRowNum();
        for (int colIndex = startColumnIndex; colIndex < startColumnIndex + 2; colIndex++) {
            for (int rowNum = 1; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null || row.getCell(colIndex) == null || row.getCell(colIndex).toString().trim().isEmpty()) {
                    return rowNum;
                }
            }
        }
        return lastRowNum + 1;
    }
}
