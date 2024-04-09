import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ExcelExecutionTimeRecorder {
    public static void writeExecutionTimes(String baseFileName, List<Double> bfsTimes, List<Double> dfsTimes, int nodeCount) throws IOException {
        String directoryPath = "src/main/resources/data_output/";
        Files.createDirectories(Paths.get(directoryPath)); // Ensure the directory exists

        String filePath = directoryPath + baseFileName;
        Workbook workbook;
        Sheet sheet;

        // Check if the file exists
        if (Files.exists(Paths.get(filePath))) {
            // Load the existing workbook
            try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
                workbook = WorkbookFactory.create(is);
            }
        } else {
            // Create a new workbook if the file doesn't exist
            workbook = new XSSFWorkbook();
        }

        sheet = getSheet(workbook);

        // Find the next empty row for the relevant node count
        int rowNumberFor10000 = getNextEmptyRow(sheet, 0);
        int rowNumberFor1000 = getNextEmptyRow(sheet, 2);

        // Decide where to write new data based on node count
        int rowNumberToWrite = nodeCount == 10000 ? rowNumberFor10000 : rowNumberFor1000;
        Row row = sheet.getRow(rowNumberToWrite);
        if (row == null) {
            row = sheet.createRow(rowNumberToWrite);
        }

        // Write new data in the relevant columns
        writeDataInColumns(bfsTimes, dfsTimes, nodeCount, row);

        // Write the workbook to the file
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            workbook.write(outputStream);
        }
        workbook.close(); // Close the workbook to release resources
    }

    private static void writeDataInColumns(List<Double> bfsTimes, List<Double> dfsTimes, int nodeCount, Row row) {
        if (nodeCount == 10000) {
            if (!bfsTimes.isEmpty()) {
                row.createCell(0).setCellValue(bfsTimes.get(0)); // Assuming one time per execution
            }
            if (!dfsTimes.isEmpty()) {
                row.createCell(1).setCellValue(dfsTimes.get(0)); // Assuming one time per execution
            }
        } else {
            if (!bfsTimes.isEmpty()) {
                row.createCell(2).setCellValue(bfsTimes.get(0)); // Assuming one time per execution
            }
            if (!dfsTimes.isEmpty()) {
                row.createCell(3).setCellValue(dfsTimes.get(0)); // Assuming one time per execution
            }
        }
    }

    private static Sheet getSheet(Workbook workbook) {
        Sheet sheet;
        sheet = workbook.getSheet("Execution Times");
        if (sheet == null) { // If the sheet doesn't exist, create a new one
            sheet = workbook.createSheet("Execution Times");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("BFS Execution Time 10000 (s)");
            headerRow.createCell(1).setCellValue("DFS Execution Time 10000 (s)");
            headerRow.createCell(2).setCellValue("BFS Execution Time 1000 (s)");
            headerRow.createCell(3).setCellValue("DFS Execution Time 1000 (s)");
        }
        return sheet;
    }

    private static int getNextEmptyRow(Sheet sheet, int columnIndex) {
        int rowNum;
        for (rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null || row.getCell(columnIndex) == null || row.getCell(columnIndex).toString().trim().isEmpty()) {
                break;
            }
        }
        return rowNum;
    }

    public static void writeMemoryUsage(String baseFileName, List<Long> bfsMemoryUsage, List<Long> dfsMemoryUsage, int nodeCount) throws IOException {
        String directoryPath = "src/main/resources/data_output/";
        Files.createDirectories(Paths.get(directoryPath)); // Ensure the directory exists

        String filePath = directoryPath.replace("ExecutionTimes", "MemoryUsage") + baseFileName; // Adjust file naming convention if necessary
        Workbook workbook;
        Sheet sheet;

        // Check if the file exists
        if (Files.exists(Paths.get(filePath))) {
            try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
                workbook = WorkbookFactory.create(is);
            }
        } else {
            workbook = new XSSFWorkbook();
        }

        sheet = getMemoryUsageSheet(workbook);

        // Similar logic for finding the next empty row and writing data
        int rowNumberToWrite = nodeCount == 10000 ? getNextEmptyRow(sheet, 0) : getNextEmptyRow(sheet, 2);
        Row row = sheet.getRow(rowNumberToWrite);
        if (row == null) {
            row = sheet.createRow(rowNumberToWrite);
        }

        writeMemoryUsageInColumns(bfsMemoryUsage, dfsMemoryUsage, nodeCount, row);

        try (OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    private static Sheet getMemoryUsageSheet(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Memory Usage"); // Try to get the sheet for memory usage
        if (sheet == null) { // If the sheet doesn't exist, create a new one
            sheet = workbook.createSheet("Memory Usage");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("BFS Memory Usage 10000 (bytes)");
            headerRow.createCell(1).setCellValue("DFS Memory Usage 10000 (bytes)");
            headerRow.createCell(2).setCellValue("BFS Memory Usage 1000 (bytes)");
            headerRow.createCell(3).setCellValue("DFS Memory Usage 1000 (bytes)");
        }
        return sheet;
    }

    private static void writeMemoryUsageInColumns(List<Long> bfsMemoryUsage, List<Long> dfsMemoryUsage, int nodeCount, Row row) {
        Cell cell;
        if (nodeCount == 10000) {
            if (!bfsMemoryUsage.isEmpty()) {
                cell = row.createCell(0, CellType.NUMERIC);
                cell.setCellValue(bfsMemoryUsage.get(0)); // Assuming one memory usage value per execution
            }
            if (!dfsMemoryUsage.isEmpty()) {
                cell = row.createCell(1, CellType.NUMERIC);
                cell.setCellValue(dfsMemoryUsage.get(0)); // Assuming one memory usage value per execution
            }
        } else {
            if (!bfsMemoryUsage.isEmpty()) {
                cell = row.createCell(2, CellType.NUMERIC);
                cell.setCellValue(bfsMemoryUsage.get(0)); // Assuming one memory usage value per execution
            }
            if (!dfsMemoryUsage.isEmpty()) {
                cell = row.createCell(3, CellType.NUMERIC);
                cell.setCellValue(dfsMemoryUsage.get(0)); // Assuming one memory usage value per execution
            }
        }
    }
}
