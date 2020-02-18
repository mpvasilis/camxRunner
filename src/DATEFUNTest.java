import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DATEFUNTest {

    @Test
    public void getDateTest() {
     DATEFUN df = new DATEFUN("20150102");
     String year = df.getYear(df.getDate());
     Assert.assertEquals("2015",year);
    }

    @Test
    public void getMonthTest() {
        DATEFUN df = new DATEFUN("20150102");
        String month = df.getMonth(df.getDate());
        Assert.assertEquals("01",month);
    }

    @Test
    public void getDayTest() {
        DATEFUN df = new DATEFUN("20150102");
        String day = df.getDay(df.getDate());
        Assert.assertEquals("02",day);
    }

    @Test
    public void calendarTest() {
        DATEFUN df = new DATEFUN();
        df.setDate("20150101");
        String ret = df.addHours(0);
        Assert.assertEquals("02",ret);

    }
}