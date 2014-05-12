package br.com.doit.commons.excel;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.webobjects.foundation.NSTimestamp;

/**
 * @author <a href="mailto:hprange@gmail.com.br">Henrique Prange</a>
 */
public class TestExcelImporter {
    private ExcelImporter importer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void extractDataUsingTheCorrectDataTypeWhenProcessingSpreadsheet() throws Exception {
        URL url = getClass().getResource("/importer/multi_types.xlsx");

        Map<String, Object> result = importer.extractRows(url).get(0);

        assertThat(result.containsKey("NAME"), is(true));
        assertThat(result.containsKey("AMOUNT"), is(true));
        assertThat(result.containsKey("DATE"), is(true));
        assertThat(result.containsKey("CURRENCY"), is(true));

        assertThat(result.get("NAME"), is((Object) "John Doe"));
        assertThat(result.get("AMOUNT"), is((Object) 123));
        assertThat(result.get("DATE"), is((Object) new NSTimestamp(new LocalDate(2014, 10, 10).toDateTimeAtStartOfDay().toDate())));
        assertThat(result.get("CURRENCY"), is((Object) new BigDecimal("1500.5")));
    }

    @Test
    public void extractOneLineOfDataWhenProcessingSpreadsheet() throws Exception {
        URL url = getClass().getResource("/importer/one_line.xlsx");

        Map<String, Object> result = importer.extractRows(url).get(0);

        assertThat(result.containsKey("NAME"), is(true));
        assertThat(result.containsKey("AMOUNT"), is(true));

        assertThat(result.get("NAME"), is((Object) "John Doe"));
        assertThat(result.get("AMOUNT"), is((Object) 123));
    }

    @Test
    public void extractOneLineOfDataWithBlankColumnWhenProcessingSpreadsheet() throws Exception {
        URL url = getClass().getResource("/importer/one_line_with_blank_column.xlsx");

        Map<String, Object> result = importer.extractRows(url).get(0);

        assertThat(result.containsKey("NAME"), is(true));
        assertThat(result.containsKey("AMOUNT"), is(true));
        assertThat(result.containsKey("DATE"), is(true));
        assertThat(result.containsKey("CURRENCY"), is(true));

        assertThat(result.get("NAME"), is((Object) "John Doe"));
        assertThat(result.get("AMOUNT"), nullValue());
        assertThat(result.get("DATE"), nullValue());
        assertThat(result.get("CURRENCY"), is((Object) new BigDecimal("1500.5")));
    }

    @Test
    public void extractTwoLinesOfDataWhenProcessingSpreadsheet() throws Exception {
        URL url = getClass().getResource("/importer/two_lines.xlsx");

        List<Map<String, Object>> results = importer.extractRows(url);

        assertThat(results.get(0).get("NAME"), is((Object) "John Doe"));
        assertThat(results.get(0).get("AMOUNT"), is((Object) 123));
        assertThat(results.get(1).get("NAME"), is((Object) "Fulano de Tal"));
        assertThat(results.get(1).get("AMOUNT"), is((Object) 321));
    }

    @Test
    public void ignoreEmptyLineWhenProcessingSpreadsheet() throws Exception {
        URL url = getClass().getResource("/importer/empty_line.xlsx");

        List<Map<String, Object>> results = importer.extractRows(url);

        assertThat(results.size(), is(1));
    }

    @Test
    public void ignoreUnidentifiedColumnsWhenProcessingSpreadsheet() throws Exception {
        URL url = getClass().getResource("/importer/one_line_with_unidentified_column.xlsx");

        List<Map<String, Object>> results = importer.extractRows(url);

        assertThat(results.get(0).keySet().size(), is(2));
    }

    @Test
    public void returnEmptyListWhenProcessingEmptySpreadsheet() throws Exception {
        URL url = getClass().getResource("/importer/empty.xlsx");

        List<Map<String, Object>> results = importer.extractRows(url);

        assertThat(results.size(), is(0));
    }

    @Before
    public void setup() {
        Map<String, Class<?>> config = new HashMap<>();

        config.put("NAME", String.class);
        config.put("AMOUNT", Integer.class);
        config.put("DATE", NSTimestamp.class);
        config.put("CURRENCY", BigDecimal.class);

        importer = new ExcelImporter(config);
    }

    @Test
    public void throwExceptionWhenProcessingSpreadsheetWithInvalidColumn() throws Exception {
        URL url = getClass().getResource("/importer/invalid_column.xlsx");

        thrown.expect(ExcelImporterException.class);
        thrown.expectMessage(is("A coluna 'INVALID' não é uma coluna reconhecida"));

        importer.extractRows(url);
    }
}
