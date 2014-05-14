package br.com.doit.commons.excel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Importador de dados de uma planilha do Excel permite extrair as linhas de uma planilha como objetos de tipos comuns
 * como String, Integer, BigDecimal e etc.
 * 
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
public class ExcelImporter {
    private static boolean isEmptyRow(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            if (Cells.toString(cell) != null) {
                return false;
            }
        }

        return true;
    }

    private final Map<String, Class<?>> config;

    /**
     * Cria uma nova instância do importador de dados do Excel usando a configuração passada por parâmetro.
     * 
     * @param config
     *            A configuração das colunas e tipos suportados por esse importador
     */
    public ExcelImporter(Map<String, Class<?>> config) {
        this.config = config;
    }

    /**
     * Extrai as linhas de uma planilha do Excel como uma lista de <code>Map</code>s de acordo com as configurações
     * passadas por parâmetro.
     * 
     * @param spreadsheetUrl
     *            A URL da planilha.
     * @return Retorna uma lista de <code>Map</code>s contendo os dados das linhas da planilha.
     */
    public List<Map<String, Object>> extractRows(URL spreadsheetUrl) {
        try (InputStream input = spreadsheetUrl.openStream()) {
            Workbook workbook = WorkbookFactory.create(input);

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.rowIterator();

            List<String> columnNames = new ArrayList<>();

            if (rows.hasNext()) {
                Row firstRow = rows.next();

                Iterator<Cell> cells = firstRow.cellIterator();

                while (cells.hasNext()) {
                    Cell cell = cells.next();

                    String columnName = cell.getStringCellValue();

                    if (!config.containsKey(columnName)) {
                        throw new ExcelImporterException("A coluna '" + columnName + "' não é uma coluna reconhecida");
                    }

                    columnNames.add(columnName);
                }
            }

            List<Map<String, Object>> convertedRows = new ArrayList<>();

            while (rows.hasNext()) {
                Row row = rows.next();

                if (isEmptyRow(row)) {
                    continue;
                }

                Map<String, Object> convertedRow = new HashMap<>();

                for (int i = 0; i < row.getPhysicalNumberOfCells() && i < columnNames.size(); i++) {
                    Cell cell = row.getCell(i, Row.CREATE_NULL_AS_BLANK);

                    String columnName = columnNames.get(i);

                    convertedRow.put(columnName, Cells.toObject(cell, config.get(columnName)));
                }

                convertedRows.add(convertedRow);
            }

            return convertedRows;
        } catch (IOException | InvalidFormatException exception) {
            throw new ExcelImporterException(exception);
        }
    }
}