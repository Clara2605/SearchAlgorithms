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
        int columnOffset = getColumnOffset(nodeCount, isTree);
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
        String[] headers = new String[]{
                "Graph BFS 10000", "Graph DFS 10000", "Graph BFS 1000", "Graph DFS 1000",
                "Graph BFS 50000", "Graph DFS 50000", "Tree BFS 10000", "Tree DFS 10000",
                "Tree BFS 1000", "Tree DFS 1000", "Tree BFS 50000", "Tree DFS 50000"
        };
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i] + (isExecutionTime ? " (s)" : " (bytes)"));
        }
    }

    private static int getColumnOffset(int nodeCount, boolean isTree) {
        int baseOffset = isTree ? 6 : 0; // Trees start after 6 columns for graphs
        if (nodeCount == 1000) {
            return baseOffset + 2;
        } else if (nodeCount == 50000) {
            return baseOffset + 4;
        } else {
            return baseOffset;
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
        for (int rowNum = 1; rowNum <= lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null || row.getCell(startColumnIndex) == null || row.getCell(startColumnIndex).toString().trim().isEmpty()) {
                return rowNum;
            }
        }
        return lastRowNum + 1;
    }
}
