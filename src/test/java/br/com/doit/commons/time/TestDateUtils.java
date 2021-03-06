package br.com.doit.commons.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 */
public class TestDateUtils {
    @Test
    public void createDateWithDefaultTimeZoneWhenConvertingFromLocalDate() throws Exception {
        LocalDate localDate = LocalDate.of(1983, 1, 7);

        Date result = DateUtils.toDate(localDate);

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(result);

        assertThat(calendar.get(Calendar.YEAR), is(1983));
        assertThat(calendar.get(Calendar.MONTH), is(0));
        assertThat(calendar.get(Calendar.DAY_OF_MONTH), is(7));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(calendar.get(Calendar.MINUTE), is(0));
        assertThat(calendar.get(Calendar.SECOND), is(0));
    }

    @Test
    public void returnNullWhenConvertingFromNullLocalDateToDate() throws Exception {
        Date result = DateUtils.toDate(null);

        assertThat(result, nullValue());
    }

    @Test
    public void createLocalDateWithDefaultTimeZoneWhenConvertingFromDate() throws Exception {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("1983-01-07");

        LocalDate result = DateUtils.toLocalDate(date);

        assertThat(result, is(LocalDate.of(1983, 1, 7)));
    }

    @Test
    public void returnNullWhenConvertingFromNullDateToLocalDate() throws Exception {
        LocalDate result = DateUtils.toLocalDate(null);

        assertThat(result, nullValue());
    }
}
