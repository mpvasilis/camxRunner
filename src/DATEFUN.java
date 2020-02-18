import java.util.Calendar;

public class DATEFUN {

    private String Date;
    Calendar DateObj = Calendar.getInstance();

    public DATEFUN(String date) {
        Date = date;
        setDate(date);
    }
    public DATEFUN() {
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
        DateObj.set(Calendar.MONTH, Integer.parseInt(this.getMonth(date))+1);
        DateObj.set(Calendar.YEAR, Integer.parseInt(this.getYear(date)));
        DateObj.set(Calendar.DAY_OF_MONTH, Integer.parseInt(this.getDay(date)));
        DateObj.set(Calendar.HOUR, 0);
        DateObj.set(Calendar.MINUTE, 0);
        DateObj.set(Calendar.SECOND, 0);

    }

    public String getYear(String date) {
        return date.substring(0, 4);
    }
    public String getMonth(String date) {
        return date.substring(4, 6);
    }
    public String getDay(String date) {
        return date.substring(6, 8);
    }

    public String addHours(int hours) {

        DateObj.add(Calendar.HOUR_OF_DAY, hours);
        DateObj.getTime();
        return DateObj.getTime().toString();

    }

}
