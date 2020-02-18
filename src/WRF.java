import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ucar.multiarray.MultiArray;
import ucar.netcdf.Attribute;
import ucar.netcdf.NetcdfFile;
import ucar.netcdf.Variable;

public class WRF {

    private String ncfile;
    private String directory;

    private String SIMULATION_START_DATE;
    private String SIMULATION_END_DATE;

    int grid_size_x;
    int grid_size_y;
    int max_time;

    private Date SIMULATION_START_DATE_date;
    private Date SIMULATION_END_DATE_date;

    private MultiArray T2Ma;

    MultiArray xlandMa;

    public void readWRFOutput() throws IOException {

        NetcdfFile nc =  new NetcdfFile(ncfile,true);

        Attribute SIMULATION_START_DATE_attr   = nc.getAttribute("SIMULATION_START_DATE");
        SIMULATION_START_DATE = SIMULATION_START_DATE_attr.getStringValue().replaceAll("_"," ");

        Attribute I_PARENT_START_attr   = nc.getAttribute("I_PARENT_START");
        String I_PARENT_START = I_PARENT_START_attr.toString().replaceAll("\\D+","");

        Attribute J_PARENT_START_attr   = nc.getAttribute("J_PARENT_START");
        String J_PARENT_START = J_PARENT_START_attr.toString().replaceAll("\\D+","");

        grid_size_x = Integer.parseInt(nc.getDimensions().get("west_east").toString().replaceAll("\\D+",""));
        grid_size_y = Integer.parseInt(nc.getDimensions().get("south_north").toString().replaceAll("\\D+",""));
        max_time = Integer.parseInt(nc.getDimensions().get("Time").toString().replaceAll("\\D+",""))-1;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp;
        try {
            SIMULATION_START_DATE_date = dateFormat.parse(SIMULATION_START_DATE);
            timestamp = new java.sql.Timestamp(SIMULATION_START_DATE_date.getTime());
        } catch(Exception e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(SIMULATION_START_DATE_date);
        calendar.add(Calendar.HOUR_OF_DAY, max_time);
        SIMULATION_END_DATE_date = calendar.getTime();
        SIMULATION_END_DATE = dateFormat.format(SIMULATION_END_DATE_date);


        Variable latitude  = nc.get("XLAT");
        int[] origin = new int[latitude .getRank()];
        int[] extent = latitude .getLengths();
        MultiArray latitudeMa = latitude .copyout(origin, extent);

        Variable longitude  = nc.get("XLONG");
        origin = new int[longitude .getRank()];
        extent = longitude .getLengths();
        MultiArray longitudeMa = longitude .copyout(origin, extent);

        Variable T2  = nc.get("T2");
        origin = new int[T2 .getRank()];
        extent = T2 .getLengths();
        T2Ma = T2 .copyout(origin, extent);



        Variable xland  = nc.get("XLAND");
        origin = new int[xland .getRank()];
        extent = xland .getLengths();
        xlandMa = xland .copyout(origin, extent);



    }

    public String getDIR() {
        File file = new File(ncfile);
        String parent = file.getParentFile().getAbsolutePath();
        return parent;
    }

    public MultiArray getT2Ma() {
        return T2Ma;
    }

    public void setT2Ma(MultiArray t2Ma) {
        T2Ma = t2Ma;
    }

    public WRF(String ncfile) {
        this.ncfile = ncfile;
    }

    public String getSIMULATION_START_DATE() {

        return SIMULATION_START_DATE.replace("00:00:00","01:00:00");
    }

    public String getSIMULATION_END_DATE() {
        return SIMULATION_END_DATE;
    }

    public int getGrid_size_x() {
        return grid_size_x;
    }

    public int getGrid_size_y() {
        return grid_size_y;
    }

    public int getMax_time() {
        return max_time;
    }

    public String getNcfile() {
        return ncfile;
    }

    public String getSIMULATION_START_DATE_format2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        return dateFormat.format(SIMULATION_START_DATE_date);
    }
    public String getSIMULATION_START_DATE_format2(int i) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(SIMULATION_START_DATE_date);
        if(i>0) calendar.add(Calendar.DATE, i);
        Date new_date = calendar.getTime();
        return dateFormat.format(new_date);
    }
    public String getSIMULATION_START_DATE_formatyyyy(int i) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(SIMULATION_START_DATE_date);
        if(i>0) calendar.add(Calendar.DATE, i);
        Date new_date = calendar.getTime();
        return dateFormat.format(new_date);
    }
    public String getSIMULATION_END_DATE_format2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        return dateFormat.format(SIMULATION_END_DATE_date);
    }
    public String getYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(SIMULATION_START_DATE_date);
    }

    public String getYearShort() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy");
        return dateFormat.format(SIMULATION_START_DATE_date);
    }
    public String getMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return dateFormat.format(SIMULATION_START_DATE_date);
    }
    public String dayswithcountStart(){
        SimpleDateFormat myFormat = new SimpleDateFormat("yyMMdd");
        String inputString1 = getYearShort()+"0101";
        String inputString2 = getSIMULATION_START_DATE_format2();

        try {
            Date date1 = myFormat.parse(inputString1);
            Date date2 = myFormat.parse(inputString2);
            long diff = date2.getTime() - date1.getTime();
            int days = (int) (diff / (1000*60*60*24))+1;
            String days_str = Integer.toString(days);
            if(days_str.length()==1) days_str="00"+days_str;
            if(days_str.length()==2) days_str="0"+days_str;
            return days_str;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public String dayswithcountEnd(){
        SimpleDateFormat myFormat = new SimpleDateFormat("yyMMdd");
        String inputString1 = getYearShort()+"0101";
        String inputString2 = getSIMULATION_END_DATE_format2();

        try {
            Date date1 = myFormat.parse(inputString1);
            Date date2 = myFormat.parse(inputString2);
            long diff = date2.getTime() - date1.getTime();
            int days = (int)  (diff / (1000*60*60*24));
            String days_str = Integer.toString(days);
            if(days_str.length()==1) days_str="00"+days_str;
            if(days_str.length()==2) days_str="0"+days_str;
            return days_str;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public int getSimulationDays(){
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inputString1 = SIMULATION_START_DATE;
        String inputString2 = SIMULATION_END_DATE;

        try {
            Date date1 = myFormat.parse(inputString1);
            Date date2 = myFormat.parse(inputString2);
            long diff = date2.getTime() - date1.getTime();
            int days = (int) (diff / (1000*60*60*24));
            return days;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getJulianMonth(){
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMdd");
        String inputString1 = getYear()+"0101";
        String inputString2 = getYear()+getEndMonth()+"01";

        try {
            Date date1 = myFormat.parse(inputString1);
            Date date2 = myFormat.parse(inputString2);
            long diff = date2.getTime() - date1.getTime();
            int days = (int) (diff / (1000*60*60*24))+1;
            String ret = Integer.toString(days);
            if(ret.length()==1) ret="00"+ret;
            if(ret.length()==2) ret="0"+ret;
            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getStartJulianDate(){
        String date = getYear()+dayswithcountStart();
        if(date.length()==1) date="00"+date;
        if(date.length()==2) date="0"+date;
        return date;
    }


    public String getEndJulianDate(){
        String date = getYear()+dayswithcountEnd();
        if(date.length()==1) date="00"+date;
        if(date.length()==2) date="0"+date;
        return date;
    }

    public String getStartDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(SIMULATION_START_DATE_date);
    }
    public String getEndDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(SIMULATION_END_DATE_date);
    }

    public String getStartMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return dateFormat.format(SIMULATION_START_DATE_date);
    }
    public String getEndMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return dateFormat.format(SIMULATION_END_DATE_date);
    }

    public String getStartYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(SIMULATION_START_DATE_date);
    }
    public String getEndYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(SIMULATION_END_DATE_date);
    }

    public String getDays(int i) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(SIMULATION_START_DATE_date);
        calendar.add(Calendar.DATE, i);
        Date new_date = calendar.getTime();
        return dateFormat.format(new_date);
    }

    public String getforeachdy() {
        StringBuilder ret= new StringBuilder();
        for (int i = 0; i < getSimulationDays(); i++) {
            ret.append( getDays(i)+" ");
        }
        return ret.toString().trim();
    }

    public boolean isFuture(){
        Date current = new Date();
        long runday = SIMULATION_START_DATE_date.getTime();
        Date rundate = new Date(runday);
        if(rundate.before(current)){
            return false;
        } else {
            return true;
        }
    }

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    // 2016 is leap year / 2017 for no leap year
    public String FORFUTURE_getyear(int year) {
     if(isLeapYear(year)) return "2016";
     if(!isLeapYear(year)) return "2015";
     return "2016";
    }


    public void createLandMask(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(filename)) {
                for (int i = 0; i < grid_size_x; i++) {
                    StringBuilder landmask_row = new StringBuilder();
                    for (int j = 0; j < grid_size_x; j++) {
                        int xland;
                        xland = (int) xlandMa.getFloat(new int[]{0, 0, i, j});
                        if (xland==2)  xland=0; // 2 for water -> 0 for water oceanic
                        landmask_row.append(xland);
                    }
                    out.println(landmask_row);
                }
        }
    }

    public String getInvYear() {
        String year="2015";
        if(Integer.parseInt(getYear())<2020){
            year = "2015";
        }
        else if(Integer.parseInt(getYear())<2030){
            year = "2020";
        }
        else if(Integer.parseInt(getYear())>=2030){
            year = "2030";
        }
        return year;
    }

    public String EPI_RUNLEN() {
        int EPI_RUNLEN = getSimulationDays() * 24 ;
        String EPI_RUNLEN_str = EPI_RUNLEN+"0000";
        int length = String.valueOf(EPI_RUNLEN).length();
        if(length==2)EPI_RUNLEN_str="0"+EPI_RUNLEN_str;
        return EPI_RUNLEN_str;
    }

    public String G_RUNLEN() {
        int G_RUNLEN = getSimulationDays() * 24 +1 ;
        String G_RUNLEN_str = G_RUNLEN+"0000";
        int length = String.valueOf(G_RUNLEN).length();
        if(length==2)G_RUNLEN_str="0"+G_RUNLEN_str;
        return G_RUNLEN_str;
    }


    public String CAMX_getalldates() {
        int days = getSimulationDays();
        String ret="";
        for (int i = 0; i < days; i++) {

            ret=ret+""+CAMX_getDayforRun(i)+"."+CAMX_getJulianDayforRun(i)+" ";
        }
        return ret.trim();
    }

    private String CAMX_getJulianDayforRun(int i) {
        int dates = Integer.parseInt(dayswithcountStart())+i;
        String date = Integer.toString(dates);
        if(date.length()==1) date="00"+date;
        if(date.length()==2) date="0"+date;
        return date;
    }

    private String CAMX_getDayforRun(int i) {
        String date = getDays(i);
        if(date.length()==1) date="0"+date;
        return date;
    }

    public String getJulianDate(int i) {
        String date = getYear()+dayswithcountStart();
        int date_old= Integer.parseInt(date)+i;
        date=Integer.toString(date_old);
        if(date.length()==1) date="00"+date;
        if(date.length()==2) date="0"+date;
        return date;
    }


    public String getMonthString(int i) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM",Locale.ENGLISH);
           if(i>0){
               Calendar calendar = Calendar.getInstance();
               calendar.setTime(SIMULATION_START_DATE_date);
               if(i>0) calendar.add(Calendar.DATE, i);
               Date new_date = calendar.getTime();
               return dateFormat.format(new_date);
           }
        return dateFormat.format(SIMULATION_START_DATE_date);
    }
}
